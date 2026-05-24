package org.assignments.product.service;


import org.assignments.product.dto.request.AddProductVendorRequest;
import org.assignments.product.dto.request.UpdateProductVendorRequest;
import org.assignments.product.dto.response.ProductVendorResponse;

import java.util.List;

public interface ProductVendorService {

    /** Add a vendor to a product */
    ProductVendorResponse addVendorToProduct(Long productId, AddProductVendorRequest request);

    /** Edit an existing product-vendor association */
    ProductVendorResponse updateProductVendor(Long productId, Long associationId, UpdateProductVendorRequest request);

    /** Get one association by its ID */
    ProductVendorResponse getAssociationById(Long productId, Long associationId);

    /** All vendor associations for a product */
    List<ProductVendorResponse> getVendorsForProduct(Long productId);

    /** All product associations for a vendor */
    List<ProductVendorResponse> getProductsForVendor(Long vendorId);

    /** Get the preferred vendor association for a product */
    ProductVendorResponse getPreferredVendor(Long productId);

    /** Set a specific association as preferred (clears others for the same product) */
    ProductVendorResponse setPreferredVendor(Long productId, Long associationId);

    /** Enable / disable an association */
    ProductVendorResponse toggleAssociationStatus(Long productId, Long associationId, boolean active);

    /** Soft-delete one association */
    void removeVendorFromProduct(Long productId, Long associationId);
}
