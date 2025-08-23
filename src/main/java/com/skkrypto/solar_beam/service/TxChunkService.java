package com.skkrypto.solar_beam.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.skkrypto.solar_beam.entity.*;
import com.skkrypto.solar_beam.entity.Transaction;
import com.skkrypto.solar_beam.proto.*;
import com.skkrypto.solar_beam.proto.Instruction;
import org.antlr.v4.runtime.Token;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TxChunkService {
    CsvMapper csvMapper = new CsvMapper();
    ObjectMapper objectMapper = new ObjectMapper();

    // 엔티티 정의
    List<com.skkrypto.solar_beam.entity.Account> accountEntities = new ArrayList<>();
    List<com.skkrypto.solar_beam.entity.Transaction> transactionEntities = new ArrayList<>();
    List<TransactionAccount> transactionAccountEntities = new ArrayList<>();

    List<com.skkrypto.solar_beam.entity.Instruction> instructionEntities = new ArrayList<>();
    List<InstructionAccount> instructionAccountEntities = new ArrayList<>();

    List<BalanceChange> balanceChangeEntities = new ArrayList<>();
    List<TokenBalanceChange> tokenBalanceChangeEntities = new ArrayList<>();

    Long slot;
    OffsetDateTime blockTime;


    public TxChunkService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    // parsing

    // tx table

    // account

    // tx account

    // copy 로직을

    // csv 코피는 간다
    // binary copy 최고속 넘어려움
    //

    // 테이블별 버퍼

    public void processChunk(
            byte[] body,
            long slot,
            OffsetDateTime blockTime,
            int chunkIndex,
            int startTxIdx
    ) throws IOException {
//        JsonFactory jf = objectMapper.getFactory();

//        List<SolanaTransactionDto> txlist = new ArrayList<>()

        slot = slot;
        blockTime = blockTime;

        TxBatch batch = TxBatch.parseFrom(body);

        int currentIdx = startTxIdx;

        for (SolanaTransaction tx: batch.getItemsList()) {
            processTx(tx, currentIdx);
            currentIdx++;
        }
    }

    private String processTx(
            SolanaTransaction tx,
            int currentIdx
    ) throws IOException {

        Meta meta = tx.getMeta();
        com.skkrypto.solar_beam.proto.Transaction tran = tx.getTransaction();

        // transaction table
        List<String> sigs = tran.getSignaturesList();
        String recentBlockhash = tran.getMessage().getRecentBlockhash();
        long fee = meta.getFee();
        long compute = meta.getComputeUnitsConsumed();
        List<String> logMsgs = meta.getLogMessagesList();

        Transaction te = Transaction.builder()
                .primarySignature(sigs.get(0))
                .blockTime(blockTime)
                .idxInBlock((short) currentIdx)
                .blockSlot(slot)
                .recentBlockhash(recentBlockhash)
                .fee(fee)
                .computeUnitsConsumed(compute)
                .build();

        // err 정의
        switch (meta.getStatus().getResultCase()) {
            case OK:
                String err = null;
                break;
            case ERR:
                switch (meta.getErr().getErrorTypeCase()) {
                    case INSTRUCTION_ERROR:
                        short errIxIdx = (short) meta.getErr().getInstructionError().getInstructionIndex();
                        short errIxCustom = (short) meta.getErr().getInstructionError().getCustom();
                        te.setErrInstructionIdx(errIxIdx);
                        te.setErrInstructionCustom(errIxCustom);
                        break;
                    case ERRORTYPE_NOT_SET:
                        String errKind = meta.getErr().getKind();
                        String errPayloadStr = JsonFormat.printer().print(meta.getErr().getPayload());
                        te.setErrKind(errKind);
                        te.setErrPayload(errPayloadStr);
                }
        }

        transactionEntities.add(te);


        // tx-accounts table
        List<AccountKey> accountKeys = tx.getTransaction().getMessage().getAccountKeysList();
        String accountSign = "";
        int j = 0;
        for (int i = 0; i < accountKeys.size(); i++) {
            AccountKey k = accountKeys.get(i);
            if (k.getSigner()) {
                accountSign = sigs.get(j);
                j++;
            }
            TransactionAccount tae = TransactionAccount.builder()
                    .txPrimarySignature(sigs.get(0))
                    .accountIndexInTx((short) i)
                    .txBlockTime(blockTime)
                    .account(k.getPubkey())
                    .signature(accountSign)
                    .isWritable(k.getWritable())
                    .sourceOfAccount(k.getSource())
                    .build();

            transactionAccountEntities.add(tae);
        }

        // innerinstruction
        List<InnerInstruction> inners = tx.getMeta().getInnerInstructionsList();
        inners.forEach(inner->{
            int idx = inner.getIndex();
            List<Integer> path = new ArrayList<>(List.of(idx));
            List<Instruction> instructions = inner.getInstructionsList();
            // 한 인덱스에 대해서
            for (int i = 0; i < instructions.size(); i++) {


                Instruction ix = instructions.get(i);

                int stackNumber = ix.getStackHeight();

//                스택 넘버 = 사이즈 크기가 같음 -> 마지막 요소 1 증가
//                스택 넘버 = 사이즈 크기 + 1 -> 0 추가
//                스택넘버 < 사이즈 크기 : 스택 넘버 이상 사이즈값 미만 클리어
//                마지막 요소 1 증가

                if (stackNumber == path.size() + 1) {
                    path.add(0);
                } else if (stackNumber == path.size()) {
                    path.set(path.size() - 1, path.get(path.size() - 1) + 1);
                } else if (stackNumber < path.size()) {
                    path.subList(stackNumber, path.size()).clear();
                    path.set(path.size() - 1, path.get(path.size() - 1) + 1);
                }

                String ixPath = path.stream().map(String::valueOf).collect(Collectors.joining("."));

                // 인스트럭션 엔티티 추가
                addIxAndIxAccount(ix, ixPath, sigs.get(0), (short) stackNumber);
            }
        });

        // instruction
        List<Instruction> ixs = tx.getTransaction().getMessage().getInstructionsList();

        for (int i = 0; i < ixs.size(); i++) {
            Instruction ix = ixs.get(i);
            addIxAndIxAccount(ix, String.valueOf(i), sigs.get(0), (short) 1);
        }




     }

     public void addIxAndIxAccount(Instruction ix, String ixPath, String prSig, short stackNumber) {
         com.skkrypto.solar_beam.entity.Instruction ie = com.skkrypto.solar_beam.entity.Instruction.builder()
                 .txPrimarySignature(prSig)
                 .txBlockTime(blockTime)
                 .ixPath(ixPath)
                 .stack(stackNumber)
                 .program(ix.getProgramId())
                 .build();

         // 인스트럭션 파싱
         switch (ix.getInstructionDetailsCase()) {
             case DATA:
                 // 여기서 프로그램 아이디별 디코딩 로직을 짜야함
                 // 만약 리스트에 없다면 날것으로 저장
                 byte[] data = ix.getData().toByteArray();
                 ie.setRawData(data);
                 break;
             case PARSED:
                 try {
                     String parsedInfoJsonString = JsonFormat.printer().print(ix.getParsed().getInfo());
                     ie.setParsedInfo(parsedInfoJsonString);
                 } catch (InvalidProtocolBufferException e) {
                     throw new RuntimeException(e);
                 }
                 String parsedType = ix.getParsed().getType();
                 ie.setParsedType(parsedType);
                 break;
         }

         List<String> ixAccs = ix.getAccountsList();

         instructionEntities.add(ie);

         for (int i = 0; i < ixAccs.size(); i++) {
             InstructionAccount iae = InstructionAccount.builder()
                     .instructionId(ie)
                     .accountPubkey(ixAccs.get(i))
                     .instructionTxBlockTime(blockTime)
                     .accountIndexInInstruction((short) i)
                     .build();
             instructionAccountEntities.add(iae);
         }
     }
}

//    /**
//     * 파라미터로 받은 데이터와 CsvSchema를 이용해 transactions 테이블용 CSV 라인을 생성합니다.
//     * CsvMapper가 모든 포매팅과 이스케이프 처리를 담당합니다.
//     *
//     * @return CSV 포맷으로 변환된 문자열 한 줄 (예: "sig1,1,123,...\n")
//     * @throws JsonProcessingException CsvMapper가 변환에 실패할 경우 발생
//     */
//
//    private String csv_tx(
//            String signature,
//            int idxInBlock,
//            long slot,
//            OffsetDateTime blockTime,
//            String recentBlockhash,
//            long fee,
//            long computeUnits,
//            List<String> logMessages
//    ) throws JsonProcessingException {
//
//        // 1. CsvSchema의 컬럼명과 동일한 key를 사용하는 Map을 생성합니다.
//        Map<String, Object> row = new LinkedHashMap<>();
//
//        // 2. 스키마에 정의된 순서대로 Map에 데이터를 담습니다.
//        row.put("primary_signature", signature);
//        row.put("idx_in_block", idxInBlock);
//        row.put("block_slot", slot);
//        row.put("block_time", blockTime.toString()); // CsvMapper가 처리할 수 있도록 문자열로 변환
//        row.put("recent_blockhash", recentBlockhash);
//        row.put("fee", fee);
//        row.put("compute_units_consumed", computeUnits);
//        row.put("log_messages", formatPostgresArray(logMessages)); // 배열 포맷팅은 여전히 필요
//
//        // 3. CsvMapper에게 스키마와 데이터를 넘겨주면, Mapper가 알아서 CSV 한 줄을 만들어줍니다.
//        // writeValueAsString은 자동으로 줄바꿈(\n)까지 포함해줍니다.
//        return csvMapper.writer(CsvSchemaProvider.TRANSACTIONS_SCHEMA)
//                .writeValueAsString(row);
//    }
//
//
//    /**
//     * 문자열 리스트를 PostgreSQL의 text 배열 형식(예: {"log1","log2"})으로 포맷합니다.
//     */
//    private String formatPostgresArray(List<String> list) {
//        if (list == null || list.isEmpty()) {
//            return null; // CsvMapper는 null을 빈 문자열로 처리하므로 그대로 둡니다.
//            // PostgreSQL COPY는 빈 문자열을 NULL로 인식합니다.
//        }
//        String content = list.stream()
//                .map(s -> s.replace("\\", "\\\\").replace("\"", "\\\""))
//                .collect(Collectors.joining("\",\"", "\"", "\""));
//        return "{" + content + "}";
//    }
//
//
//}
