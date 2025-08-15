package com.skkrypto.solar_beam.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skkrypto.solar_beam.dto.BlockMetadataDto;
import com.skkrypto.solar_beam.dto.ChunkMessageDto;
import com.skkrypto.solar_beam.entity.Block;
import com.skkrypto.solar_beam.entity.BlockId;
import com.skkrypto.solar_beam.repository.BlockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;


import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


@Service
public class OrchestraQuickNodeService {

    private static final Logger logger = LoggerFactory.getLogger(OrchestraQuickNodeService.class);

    // parsing용 오브젝트 맵퍼
    private final ObjectMapper objectMapper;
    private final BlockRepository blockRepository;
    private final RabbitTemplate rabbitTemplate;

    public OrchestraQuickNodeService(ObjectMapper objectMapper, BlockRepository blockRepository, RabbitTemplate rabbitTemplate) {
        this.objectMapper = objectMapper;
        this.blockRepository = blockRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    // 인풋: slot, 스트림 데이터
    public void orchestra(Long slot, byte[] buf) {
        JsonFactory factory = objectMapper.getFactory();
        factory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        // parser 생성 시도
        try (JsonParser parser = factory.createParser(buf, 0, buf.length)) {
            // block
            Block block = parseMetaDataAndCreateBlock(slot, parser);
            // 퍼블리싱
            Short total_chuncks = streamAndPublishChunks(parser, block.getBlockId());
            // 청크 수 저장
            block.setTotalChunks(total_chuncks);
        } catch (IOException e) {
            // 블럭 상태를 failed로 변경??
            throw new RuntimeException("Error processing stream for slot " + slot, e);
        }
    }

    // 블럭 데이터 파싱
    // 인풋: slot, parser
    // 아웃풋: block entity
    private Block parseMetaDataAndCreateBlock(Long slot, JsonParser parser) throws IOException {
        Block block = new Block();
        block.setSlot(slot);

        // 위치 [
        if (parser.nextToken() != JsonToken.START_ARRAY) {
            // 위치 {
        }

        // 위치 {
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            // 위치 "blockHeight"
            String name = parser.getCurrentName();

            if ("blockHeight".equals(name)) {
                parser.nextToken();
                block.setBlockHeight(parser.getLongValue());

            } else if ("blockTime".equals(name)) {
                parser.nextToken();
                long time = parser.getLongValue();
                Instant instant = Instant.ofEpochMilli(time);
                OffsetDateTime blockTime = OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
                block.setBlockTime(blockTime);

            } else if ("blockhash".equals(name)) {
                parser.nextToken();
                block.setBlockHash(parser.getText());

            } else if ("parentSlot".equals(name)) {
                parser.nextToken();
                block.setParentSlot(parser.getLongValue());

            } else if ("previousBlockhash".equals(name)) {
                parser.nextToken();
                block.setPreviousBlockhash(parser.getText());

            } else if ("rewards".equals(name)) {
                // rewards
                parser.nextToken();
                // [
                parser.skipChildren();
                // ]
            }

            else if ("transactions".equals(name)) {
                parser.nextToken();
                // 위치: [
                break;

            } else {
                parser.nextToken();
                parser.skipChildren();
            }
        }

        block.setStatus("PROCESSING");
        return blockRepository.save(block);
    }

    // tx 100개씩 묶기 시작
    private Short streamAndPublishChunks(JsonParser parser, BlockId blockId) throws IOException {

        if (parser.currentToken() != JsonToken.START_ARRAY) {
            throw new IOException("파서가 배열의 시작 지점에 위치하지 않습니다.");
        }
        // 위치 [


        // chunk size
        final int CHUNK_SIZE = 100;
        Short chunkCounter = 0;
        int txInChunk = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(64 * 1024);
        JsonGenerator gen = null;

        final long slot =  blockId.getSlot();
        final OffsetDateTime blockTime = blockId.getBlockTime();
        final JsonFactory jf = objectMapper.getFactory();


        // parser가 tx를 size번 동안 버퍼에 넣음
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            // 위치 {
            if (gen == null) {
                baos.reset();
                gen = jf.createGenerator(baos);
                gen.writeStartArray();
                txInChunk = 0;
            }

            gen.copyCurrentEvent(parser);
            txInChunk++;

            // if counter = size 번이면 큐에 export
            if (txInChunk == CHUNK_SIZE) {
                gen.writeEndArray();
                gen.flush();
                publishChunk(slot, blockTime, chunkCounter, baos.toByteArray(), txInChunk);
                gen.close();
                gen = null;
                chunkCounter++;
                txInChunk = 0;
            }
        }

        if (gen != null && txInChunk > 0) {
            gen.writeEndArray();
            gen.flush();
            publishChunk(slot, blockTime, chunkCounter, baos.toByteArray(), txInChunk);
            gen.close();
            gen = null;
            chunkCounter++;
        }
        return chunkCounter;
    }

    private static final String EXCHANGE = "solar-beam-exchange";
    private static final String CHUNK_ROUTING = "tx-chunk";

    private void publishChunk(
            long slot,
            OffsetDateTime blockTime,
            int chunkIndex,
            byte[] body,
            int txCount) {
        rabbitTemplate.convertAndSend(EXCHANGE, CHUNK_ROUTING, body, m -> {
            var props = m.getMessageProperties();
            props.setDeliveryMode(org.springframework.amqp.core.MessageDeliveryMode.PERSISTENT);

            props.setHeader("slot", slot);
            props.setHeader("blockTime", blockTime);
            props.setHeader("chunkIndex", chunkIndex);
            props.setHeader("txCount", txCount);

            props.setMessageId(java.util.UUID.randomUUID().toString());
            return m;
        });

        logger.info("📤 chunk published: slot={}, idx={}, txCount={}", slot, chunkIndex, txCount);
    }
}

//                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
//                logger.debug("[{}]--- 버퍼 내용물 확인 ({}개) ---", LocalDateTime.now().format(fmt), chunkBuffer.size());
//                for (JsonNode node : chunkBuffer) {
//                    // toPrettyString()을 사용하면 JSON이 예쁘게 포맷팅되어 출력됩니다.
//                    logger.debug(node.toPrettyString());
//                }
//                logger.debug("--- 버퍼 내용물 확인 종료 ---");