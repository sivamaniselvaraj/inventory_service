package org.assignments.vendor.repository;

import org.assignments.vendor.entity.VendorContactPerson;
import org.assignments.vendor.enums.ContactRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorContactPersonRepository extends JpaRepository<VendorContactPerson, Long> {

    /** All active contacts for a vendor, preferred first */
    @Query("""
           SELECT c FROM VendorContactPerson c
           WHERE c.vendor.id = :vendorId
             AND c.deleted   = false
           ORDER BY c.preferred DESC, c.role ASC, c.fullName ASC
           """)
    List<VendorContactPerson> findActiveByVendorId(@Param("vendorId") Long vendorId);

    /** All contacts for a vendor with a specific role */
    @Query("""
           SELECT c FROM VendorContactPerson c
           WHERE c.vendor.id = :vendorId
             AND c.role      = :role
             AND c.deleted   = false
           ORDER BY c.preferred DESC
           """)
    List<VendorContactPerson> findByVendorIdAndRole(
            @Param("vendorId") Long vendorId,
            @Param("role") ContactRole role);

    /** Preferred contact for a vendor */
    @Query("""
           SELECT c FROM VendorContactPerson c
           WHERE c.vendor.id = :vendorId
             AND c.preferred = true
             AND c.active    = true
             AND c.deleted   = false
           """)
    Optional<VendorContactPerson> findPreferredByVendorId(@Param("vendorId") Long vendorId);

    /** Specific contact by id scoped to vendor */
    @Query("""
           SELECT c FROM VendorContactPerson c
           WHERE c.id        = :id
             AND c.vendor.id = :vendorId
             AND c.deleted   = false
           """)
    Optional<VendorContactPerson> findByIdAndVendorId(
            @Param("id") Long id,
            @Param("vendorId") Long vendorId);

    /** Guard: check if another preferred already exists (excluding self) */
    @Query("""
           SELECT COUNT(c) > 0 FROM VendorContactPerson c
           WHERE c.vendor.id = :vendorId
             AND c.preferred = true
             AND c.deleted   = false
             AND c.id       != :excludeId
           """)
    boolean existsOtherPreferred(
            @Param("vendorId")  Long vendorId,
            @Param("excludeId") Long excludeId);

    /** Soft-delete all contacts for a vendor (called during vendor deletion) */
    @Modifying
    @Query("""
           UPDATE VendorContactPerson c
           SET c.deleted = true, c.active = false
           WHERE c.vendor.id = :vendorId
           """)
    int softDeleteAllForVendor(@Param("vendorId") Long vendorId);

    boolean existsByVendorIdAndEmailAndDeletedFalse(Long vendorId, String email);
}

