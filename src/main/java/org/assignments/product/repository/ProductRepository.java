package org.assignments.product.repository;

import org.assignments.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find active product by ID
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.deleted = false")
    Optional<Product> findActiveById(@Param("id") Long id);

    // Find by product code
    @Query("SELECT p FROM Product p WHERE LOWER(p.productCode) = LOWER(:productCode) AND p.deleted = false")
    Optional<Product> findByCode(@Param("productCode") String productCode);

    // Find all active products
    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.deleted = false ORDER BY p.name ASC")
    List<Product> findAllActive();

    // Find by category
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.deleted = false AND p.active = true")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Products supplied by a specific vendor (via the association table).
     */
    @Query("""
           SELECT DISTINCT p FROM Product p
           JOIN p.productVendors pv
           WHERE pv.vendor.id = :vendorId
             AND pv.deleted   = false
             AND pv.active    = true
             AND p.deleted    = false
           ORDER BY p.name ASC
           """)
    List<Product> findByVendorId(@Param("vendorId") Long vendorId);

    // Find products with low stock (units below reorder level)
    @Query("SELECT p FROM Product p WHERE p.deleted = false AND p.active = true AND p.unitsAvailable <= p.reorderLevel")
    List<Product> findLowStockProducts();

    // Full-text search on name or code
    @Query("SELECT p FROM Product p WHERE p.deleted = false AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Product> searchProducts(@Param("q") String query);

    // Check product code uniqueness for update
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE LOWER(p.productCode) = LOWER(:productCode) AND p.id != :excludeId AND p.deleted = false")
    boolean existsByCodeExcluding(@Param("productCode") String productCode, @Param("excludeId") Long excludeId);

    boolean existsByProductCodeIgnoreCase(String productCode);

    // Soft delete
    @Modifying
    @Query("UPDATE Product p SET p.deleted = true, p.active = false WHERE p.id = :id")
    int softDeleteById(@Param("id") Long id);
}