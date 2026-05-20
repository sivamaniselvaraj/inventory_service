package org.assignments.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {

    private Long id;
    private String productCode;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private BigDecimal sellingPrice;
    private Integer unitsAvailable;
    private String unitOfMeasure;
    private String manufacturedBy;
    private LocalDate manufacturedOn;
    private LocalDate expiryDate;
    private Integer shelfLifeDays;
    private String batchNumber;
    private Integer reorderLevel;
    private Integer maxStockLevel;
    private boolean active;
    private boolean expired;
    private boolean lowStock;
    private CategorySummary category;
    private VendorSummary vendor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategorySummary {
        private Long id;
        private String code;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VendorSummary {
        private Long id;
        private String vendorCode;
        private String name;
    }
}
