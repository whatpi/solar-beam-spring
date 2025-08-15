package com.skkrypto.solar_beam.listener;

import com.skkrypto.solar_beam.service.OrchestraQuickNodeService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrchestraListner {
    private final OrchestraQuickNodeService orchestraQuickNodeService;

    public OrchestraListner( OrchestraQuickNodeService orchestraQuickNodeService) {
        this.orchestraQuickNodeService = orchestraQuickNodeService;
    }

    @RabbitListener(queues = "orchestration-queue", containerFactory = "orchestrationContainerFactory")
    public void listen(@Header("slot") long slot, @Payload byte[] body) {
        orchestraQuickNodeService.orchestra(slot, body);
    }
}
