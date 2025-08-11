CREATE EXTENSION IF NOT EXISTS timescaledb;

CREATE TABLE accounts (
    pubkey VARCHAR(44) PRIMARY KEY,
    -- 1단계 (RPC 호출 없이): 워커는 먼저 token_balance_changes 테이블을 조회합니다. 여기에 기록된 모든 token_account_pubkey에 대해, account_metadata 테이블에 inferred_type = 'TOKEN_ACCOUNT'와 owner_pubkey를 채워 넣습니다. 이 과정은 RPC 호출이 전혀 필요 없는 매우 빠른 작업입니다.
    -- 2단계 (RPC 호출 최소화): 그 후, 아직도 account_metadata에 정보가 없는 나머지 계정들(일반 지갑, 풀 계정 등)에 대해서만 getAccountInfo RPC를 호출하여 owner 정보를 가져옵니다.
    owner_pubkey VARCHAR(44) REFERENCES accounts(pubkey),
    -- sol 잔액
    lamports BIGINT,
    -- sol 잔액이나 토큰 밸런스는 balance change에서 조회하면 됨

    -- 1단계와 2단계로 알아낸 owner 정보와 1단계의 프로그램 정보를 기반으로 종류를 추론
    -- 여기서 종류: 풀, 솔라나 지갑, 토큰 계좌인지 태그
    -- POOL. WALLET. TOKEN_ACCOUNT, TOKEN, DEX, PROGRAM
    type VARCHAR(50),
    -- fk가 아닌 이유는 전체 블럭 데이터를 못 갖고 오니까 검사기 강제를 피하기 위해
    create_account_in_tx VARCHAR(88),
    last_updated_at TIMESTAMP WITH TIME ZONE,
    data JSONB
);

-- owner가 토큰 프로그램 -> 토큰 계좌
-- owner가 orca whirlpool -> 유동성 풀
-- owner가 시스템 프로그램 -> 사용자 지갑


CREATE TABLE blocks (
    slot BIGINT NOT NULL,
    block_height BIGINT NOT NULL,
    block_hash VARCHAR(44) NOT NULL,
    -- to_timestamp() 필요
    block_time TIMESTAMP WITH TIME ZONE NOT NULL,
    parent_slot BIGINT             NOT NULL,
    previous_blockhash VARCHAR(44) NOT NULL,
    PRIMARY KEY (slot, block_time)
);

SELECT create_hypertable('blocks', 'block_time');

CREATE INDEX idx_blocks_previous_blockhash ON blocks(previous_blockhash);
CREATE INDEX idx_blocks_block_height ON blocks(block_height);

CREATE TABLE transactions (
    primary_signature VARCHAR(88) NOT NULL,
    idx_in_block SMALLINT NOT NULL,
    block_slot BIGINT NOT NULL,
    -- 제1 정규화를 포기하고 그냥 갖고오는 걸 선택, join 하기 빡셈, 그리고 fk면 timescale에서 못 받아먹음
    block_time TIMESTAMP WITH TIME ZONE NOT NULL,
    recent_blockhash VARCHAR(44) NOT NULL,
    fee BIGINT NOT NULL,
    compute_units_consumed BIGINT,
    error_message TEXT,
    version VARCHAR(20),
    -- 로그 메세지 검색을 유저가 원할 수 있음.. 따로 테이블을 만들어야할 가능성
    log_messages TEXT[],
    PRIMARY KEY (primary_signature, block_time)
--     FOREIGN KEY (block_slot, block_time) REFERENCES blocks(slot) ON DELETE CASCADE
);

CREATE INDEX idx_transactions_block_slot ON transactions(block_slot);

SELECT create_hypertable('transactions', 'block_time');

CREATE TABLE transaction_accounts (
    id BIGSERIAL NOT NULL,
    tx_primary_signature VARCHAR(88) NOT NULL,
    -- transactions 테이블이 Hypertable이므로, 참조를 위해 tx_block_time이 필수
    tx_block_time TIMESTAMP WITH TIME ZONE NOT NULL,
    -- 트랜잭션 내 계정 목록에서의 순서 (0, 1, 2...)
    account_index_in_tx SMALLINT NOT NULL,
    -- 계정의 공개 키
    account_pubkey VARCHAR(44) NOT NULL REFERENCES accounts(pubkey),
    -- 서명자의 실제 서명 값 (VARCHAR(88))
    -- 이 값이 NULL이 아니면 해당 계정은 '서명자'임을 의미함
    signature VARCHAR(88),
    -- 해당 계정이 쓰기 가능(writable)한지 여부
    is_writable BOOLEAN NOT NULL,
    -- 해당 계정이 트랜잭션 헤더에 명시되었는지, 주소 룩업 테이블을 통해 참조되었는지 등을 명시
    -- 예: 'STATIC', 'LOOKUP_WRITABLE', 'LOOKUP_READONLY'
    source_of_account VARCHAR(20) NOT NULL,
    -- 한 트랜잭션 내에서 계정의 순서는 고유하므로 이를 복합 기본 키로 사용
    PRIMARY KEY (id, tx_block_time)
--     FOREIGN KEY (tx_primary_signature, tx_block_time) REFERENCES transactions(primary_signature, block_time) ON DELETE CASCADE
);

SELECT create_hypertable('transaction_accounts', 'tx_block_time');

-- 💡 가장 빈번하게 사용될 조회: "특정 계정(account_pubkey)이 관련된 모든 트랜잭션 찾기"
-- 이 인덱스는 지갑 주소 기반의 트랜잭션 히스토리 조회 성능을 결정하는 핵심적인 역할을 합니다.
CREATE INDEX idx_transaction_accounts_pubkey ON transaction_accounts(account_pubkey);

-- 서명 값은 NULL일 수 있으므로, NULL이 아닌 값에 대해서만 인덱싱하여 효율을 높입니다.
-- 서명 값으로 직접 트랜잭션을 검색하는 기능이 필요할 경우 유용합니다.
CREATE INDEX idx_transaction_accounts_signature ON transaction_accounts(signature) WHERE signature IS NOT NULL;


-- instructions 테이블은 데이터가 매우 빠르게 증가하므로 Hypertable로 전환
CREATE TABLE instructions(
    id BIGSERIAL NOT NULL,
    tx_primary_signature VARCHAR(88) NOT NULL,
    tx_block_time TIMESTAMP WITH TIME ZONE NOT NULL,
    -- ex 2.1.1
    ix_path TEXT NOT NULL,
    depth SMALLINT NOT NULL,
    program_id VARCHAR(44) REFERENCES accounts(pubkey),
    raw_data TEXT,
    -- background woker가 비동기 처리해야
    -- 토큰 전송 수량
    parsed_info_amount INT,
    -- owner 주소
    parsed_info_authroity VARCHAR(44) REFERENCES accounts(pubkey),
    -- 목적지 토큰 계정 주소
    parsed_info_destination VARCHAR(44) REFERENCES accounts(pubkey),
    -- 출발지 토큰 계정 주소
    parsed_info_source VARCHAR(44) REFERENCES accounts(pubkey),
    -- spl 명령어 종류
    parsed_type VARCHAR(10),
    PRIMARY KEY (id, tx_block_time)
    -- 솔라나 공식 프로그램은 parsed가 따로 있음
    -- 기존 UNIQUE 제약 조건의 컬럼 오류 수정 (ix_index -> ix_path)
--     UNIQUE (tx_primary_signature, tx_block_time, ix_path),
--     FOREIGN KEY (tx_primary_signature, tx_block_time) REFERENCES transactions(primary_signature, block_time) ON DELETE CASCADE
);

CREATE INDEX idx_instructions_tx_fk ON instructions(tx_primary_signature);
CREATE INDEX idx_instructions_program_id ON instructions(program_id);

-- instructions 테이블을 `tx_block_time` 기준으로 Hypertable로 전환
SELECT create_hypertable('instructions', 'tx_block_time');


-- instructions의 PK 변경으로 인해 참조하는 instruction_accounts 테이블 수정
CREATE TABLE instruction_accounts (
    instruction_id BIGINT NOT NULL,
    -- FK 관계를 위해 부모 테이블의 PK 컬럼(tx_block_time)을 추가
    instruction_tx_block_time TIMESTAMP WITH TIME ZONE NOT NULL,
    account_pubkey VARCHAR(44) NOT NULL REFERENCES accounts(pubkey),
    account_index_in_instruction SMALLINT NOT NULL,

    PRIMARY KEY (instruction_id, account_pubkey)
    -- 수정된 instructions 테이블의 복합 PK를 참조하도록 FOREIGN KEY 변경
--     FOREIGN KEY (instruction_id) REFERENCES instructions(id) ON DELETE CASCADE
);

-- 복합 키에서 첫번 째 키를 기준으로 검색하는 것은 효율적이지만, 두 번째 키부터는 효율적이지 못함
-- 아마도 for문을 하는 듯 첫번 째 키별로 모두 접속해서 where을 던지는 식인듯
-- 그래서 두 번쨰 인덱싱 부터는 인덱스를 따로 만들어줘야 함
CREATE INDEX idx_instruction_accounts_account_pubkey ON instruction_accounts(account_pubkey);
CREATE INDEX idx_instruction_accounts_instruction_id ON instruction_accounts(instruction_id);

CREATE TABLE balance_changes(
    -- 복합키가 너무 길어짐, jpa에서는 복합키를 다루기 어려움
    id BIGSERIAL NOT NULL,
    tx_primary_signature VARCHAR(88) NOT NULL,
    tx_block_time TIMESTAMP WITH TIME ZONE NOT NULL,
    idx_in_tx SMALLINT NOT NULL,
    account_pubkey VARCHAR(44) NOT NULL REFERENCES accounts(pubkey),
    pre_balance BIGINT NOT NULL,
    post_balance BIGINT NOT NULL,
    -- 마찬가지로 정규화를 해치는 것으로 선택
    block_time TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id, tx_block_time)
);

CREATE INDEX idx_balance_changes_accounts_account_pubkey ON balance_changes(account_pubkey);

SELECT create_hypertable('balance_changes', 'tx_block_time');

CREATE TABLE token_balance_changes (
    id BIGSERIAL NOT NULL,
    tx_primary_signature VARCHAR(88) NOT NULL,
    tx_block_time TIMESTAMP WITH TIME ZONE NOT NULL,
    idx_in_tx SMALLINT NOT NULL,
    account_pubkey VARCHAR(44) NOT NULL REFERENCES accounts(pubkey),
    -- accounts에 findOrCreate
    mint_address VARCHAR(44) NOT NULL REFERENCES accounts(pubkey),
    -- accounts에 findOrCreate
    owner_address VARCHAR(44) REFERENCES accounts(pubkey),
    program_id VARCHAR(44) REFERENCES accounts(pubkey),
    pre_amount_raw VARCHAR(40),
    post_amount_raw VARCHAR(40),
    decimals SMALLINT,
    block_time TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id, tx_block_time)
);

CREATE INDEX idx_token_balance_changes_tx_fk ON token_balance_changes(tx_primary_signature);
CREATE INDEX idx_token_balance_changes_account ON token_balance_changes(account_pubkey);
CREATE INDEX idx_token_balance_changes_mint ON token_balance_changes(mint_address);
CREATE INDEX idx_token_balance_changes_owner ON token_balance_changes(owner_address);

SELECT create_hypertable('token_balance_changes', 'tx_block_time');

-- 일반 키 모음으로도 모두 표현 가능하다는 가정하에 룩업 테이블 보류
-- CREATE TABLE address_lookups (
--
-- )

-- CREATE TABLE swap_events(
--     transaction_primary_signature VARCHAR(88) NOT NULL REFERENCES transactions(primary_signature) ON DELETE CASCADE,
--     pool_address VARCHAR(44) NOT NULL REFERENCES accounts(pubkey),
--     dex_address VARCHAR(44) NOT NULL REFERENCES accounts(pubkey),
--     token_a_address VARCHAR(44) NOT NULL REFERENCES accounts(pubkey),
--     token_b_address VARCHAR(44) NOT NULL REFERENCES accounts(pubkey),
--     pool_type VARCHAR(10) CHECK (pool_type IN ('CPMM', 'CLMM')),
--     fee_bps INTEGER,
--     volume_24h NUMERIC,
--     created_at TIMESTAMPTZ DEFAULT now()
-- );


-- account metadata worker
-- balance_changes에서 찾아서 owner 명시하거나 노드에게 물어봐서 owner 찾아서 태그 해야함
-- owner 생성 -> owner의 태그 생성
-- account의 태그를 생성된 owner의 태그를 보고 생성

-- event labelling worker
-- event들을 후처리함

-- instruction data parsing worker
-- instruction data + 프로그램 아이디를 보고
-- 정의된 디코딩 방식으로 함수 수정
-- 이건 programs 테이블이 따로 있고 여기다가 일관적인 디코딩 방식을 텍스트로 저장해놓을까?

