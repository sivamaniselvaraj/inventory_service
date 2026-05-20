package org.assignments.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.assignments.inventory.enums.InventoryStatus;
import org.assignments.inventory.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderConfirmationCompletedEvent {

    private UUID correlationId;
    private UUID orderId;
    private UUID customerId;
    private OrderStatus orderStatus;
    private InventoryStatus inventoryStatus;
    private String message;
    private List<ItemInventoryResult> itemResults;
    @Builder.Default
    private String eventType = "INVENTORY_CHECK_RESULT";

    @Builder.Default
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedAt = LocalDateTime.now();
    //private ItemInventoryResult itemInventoryResult;
}