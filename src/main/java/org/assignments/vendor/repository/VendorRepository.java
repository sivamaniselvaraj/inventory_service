package org.assignments.vendor.repository;

import org.assignments.vendor.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    // Find active, non-deleted vendor by ID
    @Query("SELECT v FROM Vendor v WHERE v.id = :id AND v.deleted = false")
    Optional<Vendor> findActiveById(@Param("id") Long id);

    // Find by vendor code (case-insensitive)
    @Query("SELECT v FROM Vendor v WHERE LOWER(v.vendorCode) = LOWER(:vendorCode) AND v.deleted = false")
    Optional<Vendor> findByVendorCode(@Param("vendorCode") String vendorCode);

    // Find all active vendors
    @Query("SELECT v FROM Vendor v WHERE v.deleted = false ORDER BY v.name ASC")
    List<Vendor> findAllActive();

    // Find active + enabled vendors only
    @Query("SELECT v FROM Vendor v WHERE v.deleted = false AND v.active = true ORDER BY v.name ASC")
    List<Vendor> findAllActiveAndEnabled();

    // Search by name
    @Query("SELECT v FROM Vendor v WHERE v.deleted = false AND LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Vendor> searchByName(@Param("name") String name);

    // Check vendor code uniqueness (excluding self for update)
    @Query("SELECT COUNT(v) > 0 FROM Vendor v WHERE LOWER(v.vendorCode) = LOWER(:vendorCode) AND v.id != :excludeId AND v.deleted = false")
    boolean existsByVendorCodeExcluding(@Param("vendorCode") String vendorCode, @Param("excludeId") Long excludeId);

    boolean existsByVendorCodeIgnoreCase(String vendorCode);
}