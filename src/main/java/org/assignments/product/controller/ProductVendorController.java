package org.assignments.product.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assignments.category.dto.response.ApiResponse;
import org.assignments.product.dto.request.AddProductVendorRequest;
import org.assignments.product.dto.request.UpdateProductVendorRequest;
import org.assignments.product.dto.response.ProductVendorResponse;
import org.assignments.product.service.ProductVendorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for the product-vendor association table.
 *
 * All endpoints are nested under /products/{productId}/vendors
 * to make the ownership explicit. Vendor-centric views are
 * available via GET /vendors/{vendorId}/products on VendorController.
 *
 * Endpoints
 * ─────────────────────────────────────────────────────────────────
 * POST   /products/{productId}/vendors
 * GET    /products/{productId}/vendors
 * GET    /products/{productId}/vendors/preferred
 * GET    /products/{productId}/vendors/{associationId}
 * PUT    /products/{productId}/vendors/{associationId}
 * PATCH  /products/{productId}/vendors/{associationId}/preferred
 * PATCH  /products/{productId}/vendors/{associationId}/status?active=
 * DELETE /products/{productId}/vendors/{associationId}
 */
@RestController
@RequestMapping("/products/{productId}/vendors")
@RequiredArgsConstructor
@Slf4j
public class ProductVendorController {

    private final ProductVendorService productVendorService;

    /** Add a vendor to a product */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductVendorResponse>> addVendor(
            @PathVariable Long productId,
            @Valid @RequestBody AddProductVendorRequest request) {
        log.info("REST add vendor {} to product {}", request.getVendorId(), productId);
        ProductVendorResponse response = productVendorService.addVendorToProduct(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vendor added to product successfully", response));
    }

    /** List all vendor associations for a product */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductVendorResponse>>> getVendors(
            @PathVariable Long productId) {
        log.debug("REST get vendors for product {}", productId);
        return ResponseEntity.ok(ApiResponse.success(
                productVendorService.getVendorsForProduct(productId)));
    }

    /** Get the preferred (primary) vendor for a product */
    @GetMapping("/preferred")
    public ResponseEntity<ApiResponse<ProductVendorResponse>> getPreferredVendor(
            @PathVariable Long productId) {
        log.debug("REST get preferred vendor for product {}", productId);
        return ResponseEntity.ok(ApiResponse.success(
                productVendorService.getPreferredVendor(productId)));
    }

    /** Get a specific association by its ID */
    @GetMapping("/{associationId}")
    public ResponseEntity<ApiResponse<ProductVendorResponse>> getAssociation(
            @PathVariable Long productId,
            @PathVariable Long associationId) {
        return ResponseEntity.ok(ApiResponse.success(
                productVendorService.getAssociationById(productId, associationId)));
    }

    /** Update supply-chain metadata on an existing association */
    @PutMapping("/{associationId}")
    public ResponseEntity<ApiResponse<ProductVendorResponse>> updateAssociation(
            @PathVariable Long productId,
            @PathVariable Long associationId,
            @Valid @RequestBody UpdateProductVendorRequest request) {
        log.info("REST update association {} for product {}", associationId, productId);
        return ResponseEntity.ok(ApiResponse.success("Association updated successfully",
                productVendorService.updateProductVendor(productId, associationId, request)));
    }

    /** Promote a vendor to preferred status (demotes any previous preferred) */
    @PatchMapping("/{associationId}/preferred")
    public ResponseEntity<ApiResponse<ProductVendorResponse>> setPreferred(
            @PathVariable Long productId,
            @PathVariable Long associationId) {
        log.info("REST set association {} as preferred for product {}", associationId, productId);
        return ResponseEntity.ok(ApiResponse.success("Preferred vendor updated",
                productVendorService.setPreferredVendor(productId, associationId)));
    }

    /** Enable or disable a vendor association */
    @PatchMapping("/{associationId}/status")
    public ResponseEntity<ApiResponse<ProductVendorResponse>> toggleStatus(
            @PathVariable Long productId,
            @PathVariable Long associationId,
            @RequestParam boolean active) {
        log.info("REST set association {} active={} for product {}", associationId, active, productId);
        return ResponseEntity.ok(ApiResponse.success("Association status updated",
                productVendorService.toggleAssociationStatus(productId, associationId, active)));
    }

    /** Soft-delete (remove) a vendor from a product */
    @DeleteMapping("/{associationId}")
    public ResponseEntity<ApiResponse<Void>> removeVendor(
            @PathVariable Long productId,
            @PathVariable Long associationId) {
        log.info("REST remove association {} from product {}", associationId, productId);
        productVendorService.removeVendorFromProduct(productId, associationId);
        return ResponseEntity.ok(ApiResponse.success("Vendor removed from product", null));
    }
}
