package com.skkrypto.solar_beam.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.skkrypto.solar_beam.service.TxChunkService;

import java.io.IOException;
import java.time.OffsetDateTime;

public class ChunkListener {

    private final TxChunkService txChunkService;
    private final static Logger logger = LoggerFactory.getLogger(ChunkListener.class);

    public ChunkListener(TxChunkService txChunkService) {
        this.txChunkService = txChunkService;
    }

    @RabbitListener(queues = "tx-chunk", containerFactory = "transactionContainerFactory")
    public void listen(@Header("slot") long slot,
                       @Header("blockTime") String blockTimeStr,
                       @Header("chunkIndex") int chunkIndex,
                       @Header("startTxIdx") int startTxIdx,
                       @Payload byte[] body) throws IOException {

        final OffsetDateTime blockTime;
        blockTime = OffsetDateTime.parse(blockTimeStr);

        try {
            txChunkService.processChunk(body, slot, blockTime, chunkIndex, startTxIdx);
            logger.info("✅ chunk processed: slot={}, idx={}, startTxIdx={}, chunkSize={}", slot, chunkIndex, startTxIdx, body.length);
        } catch (Exception e) {
            logger.error("❌ chunk processing failed: slot={}, idx={}, txCount={}, chunkSize={}", slot, chunkIndex, startTxIdx, body.length,e);
            throw e;
        }
    }
}
