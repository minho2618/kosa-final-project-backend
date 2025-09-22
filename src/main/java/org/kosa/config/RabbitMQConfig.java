package org.kosa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange 정의
    public static final String ORDER_EXCHANGE = "order.exchange";

    // Queue
    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String INVENTORY_QUEUE = "inventory.queue";
    public static final String PAYMENT_QUEUE = "payment.queue";
    public static final String SHIPPING_QUEUE = "shipping.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    // Routing Key 정의
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
    public static final String INVENTORY_RESERVED_ROUTING_KEY = "inventory.reserved";
    public static final String PAYMENT_PROCESSED_ROUTING_KEY = "payment.processed";
    public static final String SHIPPING_STARTED_ROUTING_KEY = "shipping.started";

    // Dead Letter Exchange & Queue
    public static final String DLX_EXCHANGE = "order.dlx.exchange";
    public static final String DLQ_QUEUE = "order.dlq.queue";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange dlxExchange() {
        return new TopicExchange(DLX_EXCHANGE, true, false);
    }

    // Queue 정의 (Dead Letter Queue 설정 포함)
    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(ORDER_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "order.failed")
                .build();
    }

    @Bean
    public Queue inventoryQueue() {
        return QueueBuilder.durable(INVENTORY_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "inventory.failed")
                .build();
    }
    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(PAYMENT_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "payment.failed")
                .build();
    }

    @Bean
    public Queue shippingQueue() {
        return QueueBuilder.durable(SHIPPING_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "payment.failed")
                .build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    // Binding 설정
    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder.bind(orderCreatedQueue())
                .to(orderExchange())
                .with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding inventroyBinding() {
        return BindingBuilder.bind(inventoryQueue())
                .to(orderExchange())
                .with(INVENTORY_RESERVED_ROUTING_KEY);
    }

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(paymentQueue())
                .to(orderExchange())
                .with(PAYMENT_PROCESSED_ROUTING_KEY);
    }

    @Bean
    public Binding shippingBinding() {
        return BindingBuilder.bind(shippingQueue())
                .to(orderExchange())
                .with(SHIPPING_STARTED_ROUTING_KEY);
    }


    // 알림은 모든 이벤트를 받도록 설정
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(shippingQueue())
                .to(orderExchange())
                .with("order.*");
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(orderExchange())
                .with("*.failed");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Java 8 시간 모듈 등록
        objectMapper.registerModule(new JavaTimeModule());
        // ISO-8601 형식으로 날짜 직렬화
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

}
