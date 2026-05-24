package org.assignments.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.assignments.inventory.entity.BaseEntity;
import org.assignments.vendor.entity.Vendor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Rich association between a Product and its Vendors.
 *
 * Replaces the single vendor_id FK on the product table with a
 * many-to-many relationship that carries supply-chain metadata:
 *
 *  - supplyPrice      — what the vendor charges us (may differ from unitPrice)
 *  - leadTimeDays     — typical fulfilment lead time
 *  - minimumOrderQty  — MOQ the vendor requires
 *  - preferred        — marks the primary / preferred vendor for this product
 *  - contractRef      — purchase-order or contract reference number
 *  - validFrom/Until  — period this vendor arrangement is active
 *  - active / deleted — soft lifecycle identical to other entities
 */
@Entity
@Table(
    name = "product_vendor",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_product_vendor",
        columnNames = {"product_id", "vendor_id"}
    ),
    indexes = {
        @Index(name = "idx_pv_product",  columnList = "product_id"),
        @Index(name = "idx_pv_vendor",   columnList = "vendor_id"),
        @Index(name = "idx_pv_preferred",columnList = "product_id, preferred")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVendor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Associations ────────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_pv_product"))
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_pv_vendor"))
    private Vendor vendor;

    // ── Supply-chain metadata ────────────────────────────────────────
    /** Price at which this vendor supplies the product to us */
    @Column(name = "supply_price", precision = 15, scale = 2)
    private BigDecimal supplyPrice;

    /** Typical number of days from PO to delivery */
    @Column(name = "lead_time_days")
    private Integer leadTimeDays;

    /** Minimum order quantity this vendor requires */
    @Column(name = "minimum_order_qty")
    @Builder.Default
    private Integer minimumOrderQty = 1;

    /** True = this is the primary vendor used for stock replenishment */
    @Column(name = "preferred", nullable = false)
    @Builder.Default
    private boolean preferred = false;

    /** Purchase-order / contract reference for traceability */
    @Column(name = "contract_ref", length = 100)
    private String contractRef;

    /** First day this vendor arrangement is valid */
    @Column(name = "valid_from")
    private LocalDate validFrom;

    /** Last day this vendor arrangement is valid (null = open-ended) */
    @Column(name = "valid_until")
    private LocalDate validUntil;

    // ── Notes ────────────────────────────────────────────────────────
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ── Lifecycle ────────────────────────────────────────────────────

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    // ── Convenience ──────────────────────────────────────────────────
    public boolean isExpiredArrangement() {
        return validUntil != null && validUntil.isBefore(LocalDate.now());
    }
}
