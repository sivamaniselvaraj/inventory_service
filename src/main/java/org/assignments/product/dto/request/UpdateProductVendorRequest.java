package org.assignments.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Request body to edit an existing product-vendor association */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductVendorRequest {

    @DecimalMin(value = "0.0", inclusive = false, message = "Supply price must be greater than 0")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal supplyPrice;

    @Min(value = 0)
    private Integer leadTimeDays;

    @Min(value = 1)
    private Integer minimumOrderQty;

    private boolean preferred;

    @Size(max = 100)
    private String contractRef;

    private LocalDate validFrom;
    private LocalDate validUntil;

    @Size(max = 1000)
    private String notes;

    private String updatedBy;
}
