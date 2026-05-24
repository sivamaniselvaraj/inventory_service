package org.assignments.product.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Request body to add or update a vendor association on a product */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProductVendorRequest {

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    @DecimalMin(value = "0.0", inclusive = false, message = "Supply price must be greater than 0")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal supplyPrice;

    @Min(value = 0, message = "Lead time cannot be negative")
    private Integer leadTimeDays;

    @Min(value = 1, message = "Minimum order quantity must be at least 1")
    private Integer minimumOrderQty;

    /** If true all other preferred flags for this product are cleared */
    private boolean preferred;

    @Size(max = 100)
    private String contractRef;

    private LocalDate validFrom;
    private LocalDate validUntil;

    @Size(max = 1000)
    private String notes;

    private String createdBy;
}
