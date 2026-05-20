package org.assignments.vendor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assignments.vendor.dto.request.CreateVendorRequest;
import org.assignments.vendor.dto.request.UpdateVendorRequest;
import org.assignments.category.dto.response.ApiResponse;
import org.assignments.vendor.dto.response.VendorResponse;
import org.assignments.vendor.service.VendorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendors")
@RequiredArgsConstructor
@Slf4j
public class VendorController {

    private final VendorService vendorService;

    /**
     * POST /api/v1/vendors
     * Create a new vendor
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VendorResponse>> createVendor(
            @Valid @RequestBody CreateVendorRequest request) {
        log.info("REST request to create vendor: {}", request.getVendorCode());
        VendorResponse response = vendorService.createVendor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vendor created successfully", response));
    }

    /**
     * PUT /api/v1/vendors/{id}
     * Update an existing vendor
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorResponse>> updateVendor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVendorRequest request) {
        log.info("REST request to update vendor id: {}", id);
        VendorResponse response = vendorService.updateVendor(id, request);
        return ResponseEntity.ok(ApiResponse.success("Vendor updated successfully", response));
    }

    /**
     * GET /api/v1/vendors/{id}
     * Get vendor by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorResponse>> getVendorById(@PathVariable Long id) {
        log.debug("REST request to get vendor id: {}", id);
        return ResponseEntity.ok(ApiResponse.success(vendorService.getVendorById(id)));
    }

    /**
     * GET /api/v1/vendors/code/{vendorCode}
     * Get vendor by vendor code
     */
    @GetMapping("/code/{vendorCode}")
    public ResponseEntity<ApiResponse<VendorResponse>> getVendorByCode(
            @PathVariable String vendorCode) {
        log.debug("REST request to get vendor by code: {}", vendorCode);
        return ResponseEntity.ok(ApiResponse.success(vendorService.getVendorByCode(vendorCode)));
    }

    /**
     * GET /api/v1/vendors
     * Get all active vendors; optionally filter by name
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<VendorResponse>>> getAllVendors(
            @RequestParam(required = false) String search) {
        log.debug("REST request to get all vendors, search={}", search);
        List<VendorResponse> vendors = (search != null && !search.isBlank())
                ? vendorService.searchVendors(search)
                : vendorService.getAllVendors();
        return ResponseEntity.ok(ApiResponse.success(vendors));
    }

    /**
     * PATCH /api/v1/vendors/{id}/status
     * Enable or disable a vendor
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<VendorResponse>> toggleStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        log.info("REST request to set vendor id: {} active={}", id, active);
        VendorResponse response = vendorService.toggleVendorStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success("Vendor status updated", response));
    }

    /**
     * DELETE /api/v1/vendors/{id}
     * Soft delete a vendor
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVendor(@PathVariable Long id) {
        log.info("REST request to soft delete vendor id: {}", id);
        vendorService.softDeleteVendor(id);
        return ResponseEntity.ok(ApiResponse.success("Vendor deleted successfully", null));
    }
}