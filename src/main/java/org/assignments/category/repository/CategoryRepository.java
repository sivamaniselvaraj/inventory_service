package org.assignments.category.repository;

import org.assignments.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.deleted = false")
    Optional<Category> findActiveById(@Param("id") Long id);

    @Query("SELECT c FROM Category c WHERE LOWER(c.code) = LOWER(:code) AND c.deleted = false")
    Optional<Category> findByCode(@Param("code") String code);

    @Query("SELECT c FROM Category c WHERE c.deleted = false AND c.parent IS NULL ORDER BY c.name ASC")
    List<Category> findAllRootCategories();

    @Query("SELECT c FROM Category c WHERE c.deleted = false ORDER BY c.name ASC")
    List<Category> findAllActive();

    boolean existsByCodeIgnoreCase(String code);
}