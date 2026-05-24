package org.assignments.product.repository;


import org.assignments.product.entity.ProductVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVendorRepository extends JpaRepository<ProductVendor, Long> {

    /** All active associations for a product */
    @Query("""
           SELECT pv FROM ProductVendor pv
           JOIN FETCH pv.vendor v
           WHERE pv.product.id = :productId
             AND pv.deleted = false
           ORDER BY pv.preferred DESC, v.name ASC
           """)
    List<ProductVendor> findActiveByProductId(@Param("productId") Long productId);

    /** All active associations for a vendor */
    @Query("""
           SELECT pv FROM ProductVendor pv
           JOIN FETCH pv.product p
           WHERE pv.vendor.id = :vendorId
             AND pv.deleted = false
           ORDER BY p.name ASC
           """)
    List<ProductVendor> findActiveByVendorId(@Param("vendorId") Long vendorId);

    /** Preferred (primary) vendor for a product */
    @Query("""
           SELECT pv FROM ProductVendor pv
           JOIN FETCH pv.vendor
           WHERE pv.product.id = :productId
             AND pv.preferred = true
             AND pv.deleted = false
             AND pv.active = true
           """)
    Optional<ProductVendor> findPreferredByProductId(@Param("productId") Long productId);

    /** Specific association between a product and a vendor */
    @Query("""
           SELECT pv FROM ProductVendor pv
           WHERE pv.product.id = :productId
             AND pv.vendor.id  = :vendorId
             AND pv.deleted    = false
           """)
    Optional<ProductVendor> findByProductIdAndVendorId(
            @Param("productId") Long productId,
            @Param("vendorId")  Long vendorId);

    /** Guard: only one preferred vendor per product */
    @Query("""
           SELECT COUNT(pv) > 0 FROM ProductVendor pv
           WHERE pv.product.id = :productId
             AND pv.preferred  = true
             AND pv.deleted    = false
             AND pv.id        != :excludeId
           """)
    boolean existsOtherPreferredForProduct(
            @Param("productId")  Long productId,
            @Param("excludeId")  Long excludeId);

    /** Products that have NO vendor associations at all */
    @Query("""
           SELECT p.id FROM Product p
           WHERE p.deleted = false
             AND NOT EXISTS (
                 SELECT 1 FROM ProductVendor pv
                 WHERE pv.product = p AND pv.deleted = false
             )
           """)
    List<Long> findProductIdsWithNoVendor();

    /** Soft-delete all associations for a product (used during product deletion) */
    @Modifying
    @Query("""
           UPDATE ProductVendor pv
           SET pv.deleted = true, pv.active = false
           WHERE pv.product.id = :productId
           """)
    int softDeleteAllForProduct(@Param("productId") Long productId);

    /** Soft-delete all associations for a vendor (used during vendor deletion) */
    @Modifying
    @Query("""
           UPDATE ProductVendor pv
           SET pv.deleted = true, pv.active = false
           WHERE pv.vendor.id = :vendorId
           """)
    int softDeleteAllForVendor(@Param("vendorId") Long vendorId);

    boolean existsByProductIdAndVendorIdAndDeletedFalse(Long productId, Long vendorId);
}
