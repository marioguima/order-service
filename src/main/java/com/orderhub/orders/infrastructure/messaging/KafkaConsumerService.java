package com.orderhub.orders.infrastructure.messaging;

import com.orderhub.orders.domain.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    @KafkaListener(
            topics = "${app.kafka.topics.order-created}",
            groupId = "${app.kafka.consumer.group-id}"
    )
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent: orderId={}, customer={}, amount={}",
                event.orderId(),
                event.customerName(),
                event.totalAmount());

        log.info("Simulation notification sent to customer [e-mail, sms, push]: orderId={}, customer={}",
                event.orderId(), event.customerName());
    }
}
