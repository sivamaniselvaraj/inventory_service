package org.assignments.product.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    @NotBlank(message = "Product code is required")
    @Size(max = 50, message = "code must not exceed 50 characters")
    private String productCode;

    @NotBlank(message = "Product name is required")
    @Size(max = 300, message = "Product name must not exceed 300 characters")
    private String name;

    private String description;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Invalid unit price format")
    private BigDecimal unitPrice;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Selling price must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Invalid Selling price format")
    private BigDecimal sellingPrice;


    @NotNull(message = "Units available is required")
    @Min(value = 0, message = "Units available cannot be negative")
    private Integer unitsAvailable;

    @Size(max = 50, message = "Unit of measure must not exceed 50 characters")
    private String unitOfMeasure;

    @NotBlank(message = "Manufactured by is required")
    @Size(max = 300, message = "Manufacturer name must not exceed 300 characters")
    private String manufacturedBy;

    private LocalDate manufacturedOn;

    private LocalDate expiryDate;

    @Min(value = 1, message = "Shelf life days must be at least 1")
    private Integer shelfLifeDays;

    @Size(max = 100, message = "Batch number must not exceed 100 characters")
    private String batchNumber;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;

    @Min(value = 0, message = "Max stock level cannot be negative")
    private Integer maxStockLevel;

    private String createdBy;
}
