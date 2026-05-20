package org.assignments.inventory.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.protocol.types.Field;
import org.assignments.constants.ApplicationConstants;
import org.assignments.inventory.dto.response.OrderConfirmationCompletedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

    @Component
    @RequiredArgsConstructor
    @Slf4j
    public class InventoryCheckProducer {

        @Autowired
        KafkaTemplate<String, String> kafkaTemplate;

        @Value("${kafka.topics.inventory-check-result}")
        private String inventoryCheckResultTopic;

        public void sendOrderConfirmationCompleted(OrderConfirmationCompletedEvent event) {
            String key = String.valueOf(event.getOrderId());
            log.info("Publishing to topic: {} | orderId: {} | correlationId: {} | status: {}",
                    inventoryCheckResultTopic,
                    event.getOrderId(),
                    event.getCorrelationId(),
                    event.getOrderStatus());

            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(buildProducerRecord(event));

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Event published successfully | orderId: {} | correlationId: {} | partition: {} | offset: {}",
                            event.getOrderId(),
                            event.getCorrelationId(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish event | orderId: {} | correlationId: {} | error: {}",
                            event.getOrderId(),
                            event.getCorrelationId(),
                            ex.getMessage(), ex);
                }
            });
        }

        private ProducerRecord<String, String> buildProducerRecord(OrderConfirmationCompletedEvent event) {
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    inventoryCheckResultTopic,
                    null,
                    String.valueOf(event.getOrderId()),
                    serialize(event)
            );

            record.headers().add(new RecordHeader(ApplicationConstants.HEADER_CORRELATION_ID,
                    event.getCorrelationId().toString().getBytes(StandardCharsets.UTF_8)));
            record.headers().add(new RecordHeader(ApplicationConstants.HEADER_EVENT_TYPE,
                    event.getEventType().getBytes(StandardCharsets.UTF_8)));
            record.headers().add(new RecordHeader(ApplicationConstants.HEADER_ORDER_ID,
                    event.getOrderId().toString().getBytes(StandardCharsets.UTF_8)));

            return record;
        }

        public static String serialize(Object obj) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.writeValueAsString(obj);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize outbox payload", e);
            }
        }
    }