package org.assignments.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assignments.product.dto.request.CreateProductRequest;
import org.assignments.product.dto.request.UpdateProductRequest;
import org.assignments.category.dto.response.ApiResponse;
import org.assignments.product.dto.response.ProductResponse;
import org.assignments.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    @Autowired
    ProductService productService;

    /**
     * POST /api/v1/products
     * Add a new product
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        log.info("REST request to create product: {}", request.getProductCode());
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", response));
    }

    /**
     * PUT /api/v1/products/{id}
     * Edit an existing product
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        log.info("REST request to update product id: {}", id);
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", response));
    }

    /**
     * GET /api/v1/products/{id}
     * Get product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        log.debug("REST request to get product id: {}", id);
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }

    /**
     * GET /api/v1/products/code/{productCode}
     * Get product by Product code
     */
    @GetMapping("/code/{productCode}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductByProductCode(
            @PathVariable String productCode) {
        log.debug("REST request to get product by Product code: {}", productCode);
        return ResponseEntity.ok(ApiResponse.success(productService.getProductByProductCode(productCode)));
    }

    /**
     * GET /api/v1/products
     * List all products; optionally search by name or Product code
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(
            @RequestParam(required = false) String search) {
        log.debug("REST request to list products, search={}", search);
        List<ProductResponse> products = (search != null && !search.isBlank())
                ? productService.searchProducts(search)
                : productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    /**
     * GET /api/v1/products/category/{categoryId}
     * Get products by category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(
            @PathVariable Long categoryId) {
        log.debug("REST request to get products by category id: {}", categoryId);
        return ResponseEntity.ok(ApiResponse.success(productService.getProductsByCategory(categoryId)));
    }

    /**
     * GET /api/v1/products/vendor/{vendorId}
     * Get products by vendor
     */
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByVendor(
            @PathVariable Long vendorId) {
        log.debug("REST request to get products by vendor id: {}", vendorId);
        return ResponseEntity.ok(ApiResponse.success(productService.getProductsByVendor(vendorId)));
    }

    /**
     * GET /api/v1/products/low-stock
     * Get products below their reorder level
     */
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getLowStockProducts() {
        log.info("REST request to get low-stock products");
        return ResponseEntity.ok(ApiResponse.success(productService.getLowStockProducts()));
    }

    /**
     * PATCH /api/v1/products/{id}/status
     * Enable or disable a product
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProductResponse>> toggleStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        log.info("REST request to set product id: {} active={}", id, active);
        ProductResponse response = productService.toggleProductStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success("Product status updated", response));
    }

    /**
     * DELETE /api/v1/products/{id}
     * Soft delete a product
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        log.info("REST request to soft delete product id: {}", id);
        productService.softDeleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
}