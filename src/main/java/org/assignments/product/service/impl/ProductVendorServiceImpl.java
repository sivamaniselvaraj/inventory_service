package org.assignments.product.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assignments.inventory.exception.DuplicateResourceException;
import org.assignments.inventory.exception.InventoryException;
import org.assignments.inventory.exception.ResourceNotFoundException;
import org.assignments.product.dto.request.AddProductVendorRequest;
import org.assignments.product.dto.request.UpdateProductVendorRequest;
import org.assignments.product.dto.response.ProductVendorResponse;
import org.assignments.product.entity.Product;
import org.assignments.product.entity.ProductVendor;
import org.assignments.product.repository.ProductRepository;
import org.assignments.product.repository.ProductVendorRepository;
import org.assignments.product.service.ProductVendorService;
import org.assignments.vendor.entity.Vendor;
import org.assignments.vendor.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductVendorServiceImpl implements ProductVendorService {

    private final ProductVendorRepository productVendorRepository;
    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;

    // ── Add ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProductVendorResponse addVendorToProduct(Long productId, AddProductVendorRequest request) {
        log.info("Adding vendor {} to product {}", request.getVendorId(), productId);

        Product product = productRepository.findActiveById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Vendor vendor = vendorRepository.findActiveById(request.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", request.getVendorId()));

        if (productVendorRepository.existsByProductIdAndVendorIdAndDeletedFalse(productId, request.getVendorId())) {
            throw new DuplicateResourceException(
                    "ProductVendor association already exists for productId=" + productId
                    + " and vendorId=" + request.getVendorId());
        }

        // If marking as preferred, clear existing preferred flag for this product
        if (request.isPreferred()) {
            clearPreferredFlag(productId, null);
        }

        ProductVendor pv = ProductVendor.builder()
                .product(product)
                .vendor(vendor)
                .supplyPrice(request.getSupplyPrice())
                .leadTimeDays(request.getLeadTimeDays())
                .minimumOrderQty(request.getMinimumOrderQty() != null ? request.getMinimumOrderQty() : 1)
                .preferred(request.isPreferred())
                .contractRef(request.getContractRef())
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .notes(request.getNotes())
                .createdBy(request.getCreatedBy())
                .build();

        ProductVendor saved = productVendorRepository.save(pv);
        log.info("Vendor {} added to product {} with association id {}", vendor.getId(), productId, saved.getId());
        return toResponse(saved);
    }

    // ── Update ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProductVendorResponse updateProductVendor(Long productId, Long associationId,
                                                     UpdateProductVendorRequest request) {
        log.info("Updating product-vendor association id {} for product {}", associationId, productId);

        ProductVendor pv = findActiveAssociation(productId, associationId);

        if (request.isPreferred() && !pv.isPreferred()) {
            clearPreferredFlag(productId, associationId);
        }

        pv.setSupplyPrice(request.getSupplyPrice());
        pv.setLeadTimeDays(request.getLeadTimeDays());
        if (request.getMinimumOrderQty() != null) {
            pv.setMinimumOrderQty(request.getMinimumOrderQty());
        }
        pv.setPreferred(request.isPreferred());
        pv.setContractRef(request.getContractRef());
        pv.setValidFrom(request.getValidFrom());
        pv.setValidUntil(request.getValidUntil());
        pv.setNotes(request.getNotes());
        pv.setUpdatedBy(request.getUpdatedBy());
        pv.setUpdatedAt(LocalDateTime.now());

        return toResponse(productVendorRepository.save(pv));
    }

    // ── Reads ───────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ProductVendorResponse getAssociationById(Long productId, Long associationId) {
        return toResponse(findActiveAssociation(productId, associationId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVendorResponse> getVendorsForProduct(Long productId) {
        log.debug("Fetching all vendor associations for product {}", productId);
        return productVendorRepository.findActiveByProductId(productId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVendorResponse> getProductsForVendor(Long vendorId) {
        log.debug("Fetching all product associations for vendor {}", vendorId);
        return productVendorRepository.findActiveByVendorId(vendorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVendorResponse getPreferredVendor(Long productId) {
        return productVendorRepository.findPreferredByProductId(productId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Preferred vendor", "productId", productId));
    }

    // ── Business operations ─────────────────────────────────────────

    @Override
    @Transactional
    public ProductVendorResponse setPreferredVendor(Long productId, Long associationId) {
        log.info("Setting association {} as preferred vendor for product {}", associationId, productId);
        clearPreferredFlag(productId, associationId);
        ProductVendor pv = findActiveAssociation(productId, associationId);
        pv.setPreferred(true);
        pv.setUpdatedAt(LocalDateTime.now());
        return toResponse(productVendorRepository.save(pv));
    }

    @Override
    @Transactional
    public ProductVendorResponse toggleAssociationStatus(Long productId, Long associationId, boolean active) {
        log.info("Setting association {} active={} for product {}", associationId, active, productId);
        ProductVendor pv = findActiveAssociation(productId, associationId);

        // Cannot deactivate the only active vendor for a product without removing preferred flag
        if (!active && pv.isPreferred()) {
            long activeCount = productVendorRepository.findActiveByProductId(productId).size();
            if (activeCount <= 1) {
                throw new InventoryException(
                        "Cannot deactivate the sole preferred vendor for product " + productId
                        + ". Assign another preferred vendor first.");
            }
            pv.setPreferred(false);
        }

        pv.setActive(active);
        pv.setUpdatedAt(LocalDateTime.now());
        return toResponse(productVendorRepository.save(pv));
    }

    @Override
    @Transactional
    public void removeVendorFromProduct(Long productId, Long associationId) {
        log.info("Removing association {} from product {}", associationId, productId);
        ProductVendor pv = findActiveAssociation(productId, associationId);

        if (pv.isPreferred()) {
            long activeCount = productVendorRepository.findActiveByProductId(productId).size();
            if (activeCount <= 1) {
                throw new InventoryException(
                        "Cannot remove the sole preferred vendor for product " + productId
                        + ". Assign another preferred vendor before removing this one.");
            }
        }

        pv.setDeleted(true);
        pv.setActive(false);
        pv.setUpdatedAt(LocalDateTime.now());
        productVendorRepository.save(pv);
        log.info("Association {} soft-deleted from product {}", associationId, productId);
    }

    // ── Helpers ─────────────────────────────────────────────────────

    private ProductVendor findActiveAssociation(Long productId, Long associationId) {
        ProductVendor pv = productVendorRepository.findById(associationId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductVendor", "id", associationId));
        if (!pv.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("ProductVendor", "id", associationId);
        }
        if (pv.isDeleted()) {
            throw new ResourceNotFoundException("ProductVendor", "id", associationId);
        }
        return pv;
    }

    /** Clears the preferred flag on all associations for productId except excludeId */
    private void clearPreferredFlag(Long productId, Long excludeId) {
        productVendorRepository.findActiveByProductId(productId).stream()
                .filter(pv -> pv.isPreferred() && !pv.getId().equals(excludeId))
                .forEach(pv -> {
                    pv.setPreferred(false);
                    pv.setUpdatedAt(LocalDateTime.now());
                    productVendorRepository.save(pv);
                });
    }

    private ProductVendorResponse toResponse(ProductVendor pv) {
        return ProductVendorResponse.builder()
                .id(pv.getId())
                .productId(pv.getProduct().getId())
                .productHscCode(pv.getProduct().getProductCode())
                .productName(pv.getProduct().getName())
                .vendorId(pv.getVendor().getId())
                .vendorCode(pv.getVendor().getVendorCode())
                .vendorName(pv.getVendor().getVendorName())
                .vendorEmail(pv.getVendor().getEmail())
                .vendorContactPerson(pv.getVendor().getPreferredContact().get().getFullName())
                .supplyPrice(pv.getSupplyPrice())
                .leadTimeDays(pv.getLeadTimeDays())
                .minimumOrderQty(pv.getMinimumOrderQty())
                .preferred(pv.isPreferred())
                .contractRef(pv.getContractRef())
                .validFrom(pv.getValidFrom())
                .validUntil(pv.getValidUntil())
                .notes(pv.getNotes())
                .expiredArrangement(pv.isExpiredArrangement())
                .active(pv.isActive())
                .createdAt(pv.getCreatedAt())
                .updatedAt(pv.getUpdatedAt())
                .createdBy(pv.getCreatedBy())
                .updatedBy(pv.getUpdatedBy())
                .build();
    }
}
