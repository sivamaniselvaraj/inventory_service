package org.assignments.product.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.assignments.inventory.entity.BaseEntity;
import org.assignments.category.entity.Category;
import org.assignments.vendor.entity.Vendor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code", nullable = false, unique = true, length = 50)
    private String productCode;

    @Column(name = "name", nullable = false, length = 300)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "selling_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "units_available", nullable = false)
    private Integer unitsAvailable;

    @Column(name = "unit_of_measure", length = 50)
    private String unitOfMeasure;

    @Column(name = "manufactured_by", nullable = false, length = 300)
    private String manufacturedBy;

    @Column(name = "manufactured_on")
    private LocalDate manufacturedOn;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    @Column(name = "reorder_level")
    private Integer reorderLevel;

    @Column(name = "max_stock_level")
    private Integer maxStockLevel;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    public boolean hasStock(int requestedUnits) {
        return unitsAvailable != null && unitsAvailable >= requestedUnits;
    }
}