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
public class ProductVendorResponse {

    private Long id;

    // Product summary
    private Long productId;
    private String productHscCode;
    private String productName;

    // Vendor summary
    private Long vendorId;
    private String vendorCode;
    private String vendorName;
    private String vendorEmail;
    private String vendorContactPerson;

    // Association metadata
    private BigDecimal supplyPrice;
    private Integer leadTimeDays;
    private Integer minimumOrderQty;
    private boolean preferred;
    private String contractRef;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private String notes;
    private boolean expiredArrangement;

    // Lifecycle
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
