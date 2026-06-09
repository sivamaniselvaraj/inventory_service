package org.assignments.product.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.assignments.inventory.entity.BaseEntity;
import org.assignments.category.entity.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Column(name = "product_name", nullable = false, length = 300)
    private String productName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "selling_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "purchase_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "units_available", nullable = false)
    private Integer unitsAvailable;

    @Column(name = "barcode_number", nullable = false)
    private Integer barCodeNumber;

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

    /**
     * Many-to-many with Vendor via the rich ProductVendor association table.
     * Use getPreferredVendor() for the primary supplier.
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductVendor> productVendors = new ArrayList<>();

    // ── Convenience helpers ──────────────────────────────────────────

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    public boolean hasStock(int requestedUnits) {
        return unitsAvailable != null && unitsAvailable >= requestedUnits;
    }

    /** Returns the preferred (primary) vendor association, if any */
    public Optional<ProductVendor> getPreferredVendor() {
        return productVendors.stream()
                .filter(pv -> pv.isPreferred() && pv.isActive() && !pv.isDeleted())
                .findFirst();
    }

    /** Returns all active, non-deleted vendor associations */
    public List<ProductVendor> getActiveVendors() {
        return productVendors.stream()
                .filter(pv -> pv.isActive() && !pv.isDeleted())
                .toList();
    }

}