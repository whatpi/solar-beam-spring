package com.skkrypto.solar_beam.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
@AutoConfigureAfter(RabbitAutoConfiguration.class)
public class RabbitMQConfig {

    // 공용 Exchange
    public static final String EXCHANGE = "quicknode-block-exchange";

    // 큐/라우팅키
    public static final String ORCH_QUEUE = "orchestration-queue";
    public static final String ORCH_ROUTING = "orchestration";

    public static final String CHUNK_QUEUE = "tx-chunk-queue";
    public static final String CHUNK_ROUTING = "tx-chunk";

    public static final String DLQ_QUEUE = "dlq-queue";
    public static final String DLQ_ROUTING = "dlq";

    public static final String FINALIZE_QUEUE = "finalize-queue";
    public static final String FINALIZE_ROUTING = "finalize";

    // Exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    // 큐
    @Bean
    public Queue orchestrationQueue() {
        return QueueBuilder.durable(ORCH_QUEUE).build();
    }

    @Bean
    public Queue chunkQueue() {
        return QueueBuilder.durable(CHUNK_QUEUE)
                // 실패 시 DLQ로 보내기
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING)
                .build();
    }

    @Bean
    public Queue finalizeQue() {
        return QueueBuilder.durable(FINALIZE_QUEUE).build();
    }

    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    // 바인딩
    @Bean
    public Binding bindOrchestrationQueue(Queue orchestrationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(orchestrationQueue)
                .to(exchange)
                .with(ORCH_ROUTING);
    }

    @Bean
    public Binding bindChunkQueue(Queue chunkQueue, TopicExchange exchange) {
        return BindingBuilder.bind(chunkQueue)
                .to(exchange)
                .with(CHUNK_ROUTING);
    }

    @Bean
    public Binding bindDlqQueue(Queue dlqQueue, TopicExchange exchange) {
        return BindingBuilder.bind(dlqQueue)
                .to(exchange)
                .with(DLQ_ROUTING);
    }

    @Bean
    public Binding bindFinalizingQueue(Queue finalizeQue, TopicExchange exchange) {
        return BindingBuilder.bind(finalizeQue)
                .to(exchange)
                .with(FINALIZE_ROUTING);
    }

    // 컨테이너 팩토리: orchestration 큐
    @Bean
    public SimpleRabbitListenerContainerFactory orchestrationContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(4);
        factory.setPrefetchCount(1); // 큰 메시지는 1~2 권장
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory finalizingContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        return factory;
    }

    // 컨테이너 팩토리: chunk 큐 (Retry + DLQ)
    @Bean
    public SimpleRabbitListenerContainerFactory transactionContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            RabbitTemplate template) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConcurrentConsumers(8);
        factory.setMaxConcurrentConsumers(16);
        factory.setPrefetchCount(1);
        factory.setDefaultRequeueRejected(false); // 실패 메시지를 재큐하지 않음

        var retry = RetryInterceptorBuilder.stateless()
                .maxAttempts(3)                    // 재시도 횟수
                .backOffOptions(1000, 2.0, 15000)  // 1s → 2x 증가 → 최대 15s
                .recoverer(new RepublishMessageRecoverer(template, EXCHANGE, DLQ_ROUTING))
                .build();
        factory.setAdviceChain(retry);

        return factory;
    }
}
