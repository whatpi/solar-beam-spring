package com.skkrypto.solar_beam.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;



@Configuration
@AutoConfigureAfter(RabbitAutoConfiguration.class)
public class RabbitMQConfig {

    public static final String EXCHANGE = "solar-beam-exchange";

    public static final String ORCH_QUEUE = "orchestration-queue";
    public static final String ORCH_ROUTING = "orchestration";

    public static final String CHUNK_QUEUE = "tx-chunk-queue";
    public static final String CHUNK_ROUTING = "tx-chunk";

    @Bean
    public TopicExchange solarBeamExchange() {
        return new TopicExchange(EXCHANGE);
    }

    // 큐
    @Bean
    public Queue orchestrationQueue() {
        return new Queue(ORCH_QUEUE, true);
    }

    @Bean
    public Queue chunkQueue() {
        return new Queue(CHUNK_QUEUE, true);
    }

    // 바인딩
    @Bean
    public Binding bindingOrchestrationQueue(Queue orchestrationQueue, TopicExchange solarBeamExchange) {
        return BindingBuilder.bind(orchestrationQueue)
                .to(solarBeamExchange)
                .with(ORCH_ROUTING); // orchestration.start → orchestration-queue
    }

    @Bean
    public Binding bindingChunkQueue(Queue chunkQueue, TopicExchange solarBeamExchange) {
        return BindingBuilder.bind(chunkQueue)
                .to(solarBeamExchange)
                .with(CHUNK_ROUTING); // orchestration.chunk → chunk-queue
    }

    // 컨테이너 팩토리: 오케스트레이션(입력 큐)
    @Bean
    public SimpleRabbitListenerContainerFactory orchestrationContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(4);
        factory.setPrefetchCount(1); // 큰 메시지면 1~2 권장
        return factory;
    }

    // 컨테이너 팩토리: 청크(출력 큐 소비자들)
    @Bean
    public SimpleRabbitListenerContainerFactory transactionContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConcurrentConsumers(8);
        factory.setMaxConcurrentConsumers(16);
        factory.setPrefetchCount(1);
        return factory;
    }
}
