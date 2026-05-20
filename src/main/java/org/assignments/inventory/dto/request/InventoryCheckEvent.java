package org.assignments.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

// Outbound: to Inventory service
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryCheckEvent {
    private UUID jobId;
    private UUID orderId;
    private UUID customerId;
    private List<OrderItem> items;
    private String status;
}