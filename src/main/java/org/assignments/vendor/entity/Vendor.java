package org.assignments.vendor.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.assignments.inventory.entity.BaseEntity;
import org.assignments.product.entity.ProductVendor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "vendor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Vendor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_code", nullable = false, unique = true, length = 50)
    private String vendorCode;

    @Column(name = "vendor_name", nullable = false, length = 200)
    private String vendorName;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "gstin", length = 20)
    private String gstin;

    @Column(name = "pan", length = 20)
    private String pan;

    @Column(name = "contract_start_date")
    private LocalDate contractStartDate;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    /** One vendor → many contact persons (replaces single contact_person column) */
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VendorContactPerson> contactPersons = new ArrayList<>();

    /** Reverse side of the product_vendor association */
    @OneToMany(mappedBy = "vendor", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductVendor> productVendors = new ArrayList<>();

    /** Convenience: returns the preferred contact, if any */
    public Optional<VendorContactPerson> getPreferredContact() {
        return contactPersons.stream()
                .filter(c -> c.isPreferred() && c.isActive() && !c.isDeleted())
                .findFirst();
    }

    /** Returns all active, non-deleted contact persons */
    public List<VendorContactPerson> getActiveContacts() {
        return contactPersons.stream()
                .filter(c -> c.isActive() && !c.isDeleted())
                .toList();
    }

}