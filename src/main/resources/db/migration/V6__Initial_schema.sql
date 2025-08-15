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
    status VARCHAR(20) NOT NULL,
    total_chunks SMALLINT,
    process_chunks SMALLINT DEFAULT 0,
    PRIMARY KEY (slot, block_time)
);

SELECT create_hypertable('blocks', 'block_time');

CREATE INDEX idx_blocks_previous_blockhash ON blocks(previous_blockhash);
-- CREATE INDEX idx_blocks_block_height ON blocks(block_height);

CREATE TABLE transactions (
    primary_signature VARCHAR(88) NOT NULL,
    idx_in_block SMALLINT NOT NULL,
    block_slot BIGINT NOT NULL,
    -- 제1 정규화를 포기하고 그냥 갖고오는 걸 선택, join 하기 빡셈, 그리고 fk면 timescale에서 못 받아먹음
    block_time TIMESTAMP WITH TIME ZONE NOT NULL,
    recent_blockhash VARCHAR(44) NOT NULL,
    fee BIGINT NOT NULL,
    compute_units_consumed BIGINT,
--     error_message TEXT,
    -- 에러 있는 애들은 안 받는 걸로
    -- version VARCHAR(10),
    -- 로그 메세지 검색을 유저가 원할 수 있음.. 따로 테이블을 만들어야할 가능성
    log_messages TEXT[],
    PRIMARY KEY (primary_signature, block_time)
--     FOREIGN KEY (block_slot, block_time) REFERENCES blocks(slot) ON DELETE CASCADE
);

CREATE INDEX idx_transactions_block_slot ON transactions(block_slot);

SELECT create_hypertable('transactions', 'block_time');

CREATE TABLE transaction_accounts (
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
    PRIMARY KEY (tx_primary_signature, account_index_in_tx, tx_block_time)
--     FOREIGN KEY (tx_primary_signature, tx_block_time) REFERENCES transactions(primary_signature, block_time) ON DELETE CASCADE
);

SELECT create_hypertable('transaction_accounts', 'tx_block_time');

-- 💡 가장 빈번하게 사용될 조회: "특정 계정(account_pubkey)이 관련된 모든 트랜잭션 찾기"
-- 이 인덱스는 지갑 주소 기반의 트랜잭션 히스토리 조회 성능을 결정하는 핵심적인 역할을 합니다.
CREATE INDEX idx_transaction_accounts_pubkey ON transaction_accounts(account_pubkey);

-- 서명 값은 NULL일 수 있으므로, NULL이 아닌 값에 대해서만 인덱싱하여 효율을 높입니다.
-- 서명 값으로 직접 트랜잭션을 검색하는 기능이 필요할 경우 유용합니다.
-- CREATE INDEX idx_transaction_accounts_signature ON transaction_accounts(signature) WHERE signature IS NOT NULL;


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
    -- spl 명령어 종류
    parsed_info JSONB,
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
    instruction_accounts_id BIGSERIAL NOT NULL,
    instruction_id BIGINT NOT NULL,
    -- FK 관계를 위해 부모 테이블의 PK 컬럼(tx_block_time)을 추가
    instruction_tx_block_time TIMESTAMP WITH TIME ZONE NOT NULL,
    account_pubkey VARCHAR(44) NOT NULL REFERENCES accounts(pubkey),
    account_index_in_instruction SMALLINT NOT NULL,

    PRIMARY KEY (instruction_accounts_id, instruction_tx_block_time)
    -- 수정된 instructions 테이블의 복합 PK를 참조하도록 FOREIGN KEY 변경
--     FOREIGN KEY (instruction_id) REFERENCES instructions(id) ON DELETE CASCADE
);

SELECT create_hypertable('instruction_accounts', 'instruction_accounts.instruction_tx_block_time');

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
    -- 사실 tx_signature, idx_in_tx, tx_block_time으로 가는게 나았을듯
    -- id로 조회할 일이 없지 않나? 아 그게 있나? swap_events에 대해서?
    PRIMARY KEY (id, tx_block_time)
);

CREATE INDEX idx_balance_changes_transactions_tx_primary_signature ON balance_changes(tx_primary_signature);
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

CREATE TABLE pools (
    id BIGSERIAL PRIMARY KEY,
    name TEXT,
    dex TEXT,
    reserve_a_pubkey VARCHAR(44) NOT NULL REFERENCES accounts(pubkey),
    reserve_b_pubkey VARCHAR(44) NOT NULL REFERENCES accounts(pubkey),
    mint_a VARCHAR(44) NOT NULL REFERENCES accounts(pubkey),
    mint_b VARCHAR(44) NOT NULL REFERENCES accounts(pubkey),
--     lp_mint VARCHAR(44) REFERENCES accounts(pubkey), -- 옵션
--     fee_bps INTEGER,                                  -- 옵션
    UNIQUE (reserve_a_pubkey, reserve_b_pubkey)
);

-- todo
    -- timescale에 특화된 복합 인덱싱을 도입?
    -- 풀 메타 테이블
    -- 트랜잭션별 리저브 스냅샷 테이블
CREATE TABLE pool_reserve_snapshots (
    pool_id BIGINT NOT NULL REFERENCES pools(id),
    tx_primary_signature VARCHAR(88) NOT NULL,
    tx_block_time TIMESTAMPTZ NOT NULL,
    reserve_a NUMERIC NOT NULL,   -- 소수점 정규화(= post / 10^decimals)
    reserve_b NUMERIC NOT NULL,
    PRIMARY KEY (pool_id, tx_primary_signature)
);
SELECT create_hypertable('pool_reserve_snapshots', 'tx_block_time', if_not_exists => TRUE);
CREATE INDEX IF NOT EXISTS idx_prs_time ON pool_reserve_snapshots(pool_id, tx_block_time DESC);


-- TimescaleDB background job, cron, pg-cron이 수행합니다
-- 적재하는 쿼리
-- WITH latest AS (
--     -- 아직 스냅샷에 없는 트랜잭션만
--     SELECT t.tx_primary_signature, t.tx_block_time, t.account_pubkey,
--            t.post_amount_raw::numeric AS post_raw, t.decimals
--     FROM token_balance_changes t
--              JOIN pools p ON t.account_pubkey IN (p.reserve_a_pubkey, p.reserve_b_pubkey)
--              LEFT JOIN pool_reserve_snapshots s
--                        ON s.pool_id = p.id AND s.tx_primary_signature = t.tx_primary_signature
--     WHERE s.tx_primary_signature IS NULL
-- ),
--      paired AS (
--          SELECT
--              p.id AS pool_id,
--              l.tx_primary_signature,
--              MIN(l.tx_block_time) AS tx_block_time, -- 동일 tx 내 동일 타임스탬프
--              MAX(CASE WHEN l.account_pubkey = p.reserve_a_pubkey
--                           THEN l.post_raw / (10 ^ l.decimals) END) AS reserve_a,
--              MAX(CASE WHEN l.account_pubkey = p.reserve_b_pubkey
--                           THEN l.post_raw / (10 ^ l.decimals) END) AS reserve_b
--          FROM latest l
--                   JOIN pools p ON l.account_pubkey IN (p.reserve_a_pubkey, p.reserve_b_pubkey)
--          GROUP BY p.id, l.tx_primary_signature
--      )
-- INSERT INTO pool_reserve_snapshots (pool_id, tx_primary_signature, tx_block_time, reserve_a, reserve_b)
-- SELECT pool_id, tx_primary_signature, tx_block_time, reserve_a, reserve_b
-- FROM paired
-- WHERE reserve_a IS NOT NULL AND reserve_b IS NOT NULL
-- ON CONFLICT (pool_id, tx_primary_signature) DO NOTHING;

-- Continuous Aggregate
CREATE MATERIALIZED VIEW pool_price_ohlc_5m
WITH (timescaledb.continuous) AS
SELECT
    pool_id,
    time_bucket('5 minutes', tx_block_time) AS bucket,
    first(reserve_b / NULLIF(reserve_a, 0), tx_block_time) AS open,
    max(reserve_b / NULLIF(reserve_a, 0)) AS high,
    min(reserve_b / NULLIF(reserve_a, 0)) AS low,
    last(reserve_b / NULLIF(reserve_a, 0), tx_block_time) AS close
FROM pool_reserve_snapshots
GROUP BY pool_id, bucket
WITH NO DATA;

-- 자동 갱신 정책
-- 최신 구간만 주기적으로 리프레시함
-- timescaledb.enable_real_time_aggregate = ON
SELECT add_continuous_aggregate_policy('pool_price_ohlc_5m',
                                       start_offset => INTERVAL '3 days',
                                       end_offset   => INTERVAL '1 minute',
                                       schedule_interval => INTERVAL '1 minute');
-- CREATE MATERIALIZED VIEW pool_tvl_usd_5m
--             WITH (timescaledb.continuous) AS
-- WITH snap AS (
--     SELECT
--         s.pool_id,
--         time_bucket('5 minutes', s.tx_block_time) AS bucket,
--         last(s.reserve_a, s.tx_block_time) AS reserve_a,
--         last(s.reserve_b, s.tx_block_time) AS reserve_b
--     FROM pool_reserve_snapshots s
--     GROUP BY s.pool_id, bucket
-- ),
--      priced AS (
--          SELECT
--              snap.pool_id,
--              snap.bucket,
--              snap.reserve_a * pa.price_usd + snap.reserve_b * pb.price_usd AS tvl_usd
--          FROM snap
--                   JOIN pools p ON p.id = snap.pool_id
--                   JOIN LATERAL (
--              SELECT price_usd
--              FROM prices
--              WHERE symbol = (SELECT name FROM accounts WHERE pubkey = p.mint_a) -- 또는 심볼 매핑 테이블 사용
--                AND ts = snap.bucket
--              ) pa ON TRUE
--                   JOIN LATERAL (
--              SELECT price_usd
--              FROM prices
--              WHERE symbol = (SELECT name FROM accounts WHERE pubkey = p.mint_b)
--                AND ts = snap.bucket
--              ) pb ON TRUE
--      )
-- SELECT pool_id, bucket, AVG(tvl_usd) AS tvl_usd
-- FROM priced
-- GROUP BY pool_id, bucket
-- WITH NO DATA;
--
-- SELECT add_continuous_aggregate_policy('pool_tvl_usd_5m',
--                                        start_offset => INTERVAL '3 days',
--                                        end_offset   => INTERVAL '1 minute',
--                                        schedule_interval => INTERVAL '1 minute');


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



--- staging

