package org.assignments.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderConfirmationRequest {

    private String correlationId;
    private String orderId;
    private String customerId;
    private String customerEmail;
    private List<OrderItemRequest> items;
    private String currency;
    private BigDecimal totalAmount;
    private String requestedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderItemRequest {
        private String productId;
        private String productCode;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
