package org.assignments.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private BigDecimal purchasingPrice;
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
    /**
     * All active vendor associations (was a single VendorSummary before).
     * Use preferredVendor for the primary supplier.
     */
    private List<VendorAssociation> vendors;
    private VendorAssociation preferredVendor;
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

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class VendorAssociation {
        private Long associationId;
        private Long vendorId;
        private String vendorCode;
        private String vendorName;
        private BigDecimal supplyPrice;
        private Integer leadTimeDays;
        private Integer minimumOrderQty;
        private boolean preferred;
        private String contractRef;
        private LocalDate validFrom;
        private LocalDate validUntil;
    }

}
