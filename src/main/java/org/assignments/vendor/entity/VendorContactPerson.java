package org.assignments.vendor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.assignments.inventory.entity.BaseEntity;
import org.assignments.vendor.enums.ContactRole;

import java.time.LocalDateTime;

/**
 * Represents one contact person associated with a Vendor.
 *
 * A vendor may have many contacts, each with a distinct role
 * (PRIMARY, SALES, ACCOUNTS, LOGISTICS, etc.).
 * Only one contact per vendor may hold the PRIMARY role at a time
 * — enforced at the service layer.
 *
 * Columns:
 *   vendor_id      — FK to vendor
 *   full_name      — contact's full name
 *   role           — ContactRole enum (PRIMARY, SALES, …)
 *   email          — direct email
 *   phone          — direct phone / mobile
 *   alternate_phone— secondary number (optional)
 *   designation    — job title (e.g. "Regional Sales Manager")
 *   department     — department within the vendor org
 *   preferred      — TRUE = default contact when role is ambiguous
 *   active/deleted — soft lifecycle
 */
@Entity
@Table(
        name = "vendor_contact_person",
        indexes = {
                @Index(name = "idx_vcp_vendor",    columnList = "vendor_id"),
                @Index(name = "idx_vcp_role",      columnList = "vendor_id, role"),
                @Index(name = "idx_vcp_primary",   columnList = "vendor_id, preferred"),
                @Index(name = "idx_vcp_deleted",   columnList = "vendor_id, deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorContactPerson extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_vcp_vendor"))
    private Vendor vendor;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private ContactRole role;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "alternate_phone", length = 20)
    private String alternatePhone;

    @Column(name = "designation", length = 150)
    private String designation;

    @Column(name = "department", length = 100)
    private String department;

    /** TRUE = this is the go-to contact when role is not specified */
    @Column(name = "preferred", nullable = false)
    @Builder.Default
    private boolean preferred = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;


    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
