package com.skkrypto.solar_beam.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.skkrypto.solar_beam.service.TxChunkService;

import java.time.OffsetDateTime;

public class ChunkListener {

    private final TxChunkService txChunkService;

    public ChunkListener(TxChunkService txChunkService) {
        this.txChunkService = txChunkService;
    }

    @RabbitListener(queues = "tx-chunk-queue", containerFactory = "transactionContainerFactory")
    public void listen(@Header("slot") long slot, @Header("blockTime") OffsetDateTime blockTime, @Header("chunkIndex") int chunkIndex, @Header("txCount") int txCount, @Payload byte[] body) {
        txChunkService.
    }
}
