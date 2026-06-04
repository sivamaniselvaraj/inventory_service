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
public class UpdateProductRequest {

    @Size(max = 300, message = "Product name must not exceed 300 characters")
    private String name;

    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Invalid unit price format")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Selling price must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Invalid Selling price format")
    private BigDecimal sellingPrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Purchasing price must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Invalid Purchasing price format")
    private BigDecimal purchasingPrice;


    @Min(value = 0, message = "Units available cannot be negative")
    private Integer unitsAvailable;

    @Size(max = 50, message = "Unit of measure must not exceed 50 characters")
    private String unitOfMeasure;

    @Size(max = 300, message = "Manufacturer name must not exceed 300 characters")
    private String manufacturedBy;

    private LocalDate manufacturedOn;

    private LocalDate expiryDate;

    @Min(value = 1, message = "Shelf life days must be at least 1")
    private Integer shelfLifeDays;

    @Size(max = 100, message = "Batch number must not exceed 100 characters")
    private String batchNumber;

    private Long categoryId;

    //vendors are managed via POST /products/{id}/vendors

    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;

    @Min(value = 0, message = "Max stock level cannot be negative")
    private Integer maxStockLevel;

    private Boolean active;

    private String updatedBy;
}