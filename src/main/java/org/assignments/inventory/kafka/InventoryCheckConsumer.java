package org.assignments.inventory.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assignments.constants.ApplicationConstants;
import org.assignments.inventory.dto.request.InventoryCheckEvent;
import org.assignments.inventory.dto.request.OrderConfirmationRequest;
import org.assignments.inventory.dto.request.OrderItem;
import org.assignments.inventory.dto.response.ItemInventoryResult;
import org.assignments.inventory.dto.response.OrderConfirmationCompletedEvent;
import org.assignments.inventory.enums.InventoryStatus;
import org.assignments.inventory.enums.OrderStatus;
import org.assignments.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryCheckConsumer {

    @Autowired
    ProductService productService;

    @Autowired
    InventoryCheckProducer inventoryCheckProducer;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
            topics = "${kafka.topics.inventory-check-requested}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "${kafka.consumer.concurrency}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, String> record,
                        //@Payload InventoryCheckEvent request,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                        @Header(KafkaHeaders.OFFSET) long offset,
                        Acknowledgment acknowledgment) {

        String correlationId = extractHeader(record, ApplicationConstants.HEADER_CORRELATION_ID);
        String eventType     = extractHeader(record, ApplicationConstants.HEADER_EVENT_TYPE);
        String orderId     = extractHeader(record, ApplicationConstants.HEADER_ORDER_ID);

        // Set correlationId from payload into MDC for log tracing
        //CorrelationIdUtil.set(request.getCorrelationId());
        log.info("Received order confirmation request | topic: {} | partition: {} | offset: {} | orderId: {} | correlationId: {} | event type: {} | record value: {}",
                topic, partition, offset, orderId, correlationId, eventType, record.value());

        try {
            InventoryCheckEvent request = objectMapper.readValue(record.value(), InventoryCheckEvent.class);
            OrderConfirmationCompletedEvent completedEvent = processInventoryCheck(request, correlationId);
            inventoryCheckProducer.sendOrderConfirmationCompleted(completedEvent);
            acknowledgment.acknowledge();

            log.info("Order confirmation processed | orderId: {} | correlationId: {} | status: {}",
                    request.getOrderId(), correlationId, completedEvent.getOrderStatus());

        } catch (Exception ex) {
            log.error("Error processing order confirmation | orderId: {} | correlationId: {} | error: {}",
                    orderId, correlationId, ex.getMessage(), ex);
            // Acknowledge to avoid reprocessing a poison pill; dead-letter queue should be configured separately
            acknowledgment.acknowledge();
        } finally {
            //CorrelationIdUtil.clear();
        }
    }

    private OrderConfirmationCompletedEvent processInventoryCheck(InventoryCheckEvent request, String correlationID) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            log.warn("Order has no items | orderId: {} | correlationId: {}",
                    request.getOrderId(), correlationID);
            return buildEvent(request, OrderStatus.REJECTED, InventoryStatus.OUT_OF_STOCK,
                    "Order contains no items", new ArrayList<>(),correlationID);
        }

        List<ItemInventoryResult> itemResults = new ArrayList<>();
        boolean allAvailable = true;

        for (OrderItem item : request.getItems()) {
            log.debug("Checking inventory for productHscCode: {} | quantity: {} | correlationId: {}",
                    item.getId(), item.getQuantity(), correlationID);

            boolean available = productService.checkAndReserveStock(
                    String.valueOf(item.getId()), item.getQuantity());

            if (!available) {
                allAvailable = false;
                log.warn("Inventory unavailable for productHscCode: {} | requested: {} | correlationId: {}",
                        item.getId(), item.getQuantity(), correlationID);
            }

            itemResults.add(ItemInventoryResult.builder()
                    .productCode(String.valueOf(item.getId()))
                    .productName(item.getName())
                    .requestedQuantity(item.getQuantity())
                    .available(available)
                    .reason(available ? "Stock reserved successfully"
                            : "Insufficient or unavailable stock")
                    .build());
        }

        if (allAvailable) {
            log.info("All items available | orderId: {} | correlationId: {}",
                    request.getOrderId(), correlationID);
            return buildEvent(request, OrderStatus.CONFIRMED, InventoryStatus.AVAILABLE,
                    "All items are available. Order confirmed.", itemResults, correlationID);
        } else {
            log.warn("Some items unavailable | orderId: {} | correlationId: {}",
                    request.getOrderId(), correlationID);
            return buildEvent(request, OrderStatus.REJECTED, InventoryStatus.INSUFFICIENT_STOCK,
                    "One or more items are not available in sufficient quantity.", itemResults, correlationID);
        }
    }

    private OrderConfirmationCompletedEvent buildEvent(
            InventoryCheckEvent request,
            OrderStatus orderStatus,
            InventoryStatus inventoryStatus,
            String message,
            List<ItemInventoryResult> itemResults, String correlationId) {

        return OrderConfirmationCompletedEvent.builder()
                .correlationId(UUID.fromString(correlationId))
                .orderId(request.getOrderId())
                .customerId(request.getCustomerId())
                .orderStatus(orderStatus)
                .inventoryStatus(inventoryStatus)
                .message(message)
                .itemResults(itemResults)
                .build();
    }

    private String extractHeader(ConsumerRecord<?, ?> record, String headerName) {
        org.apache.kafka.common.header.Header header = record.headers().lastHeader(headerName);
        return header != null
                ? new String(header.value(), StandardCharsets.UTF_8)
                : "N/A";
    }
}

