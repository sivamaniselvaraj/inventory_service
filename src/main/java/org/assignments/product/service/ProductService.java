package org.assignments.product.service;

import org.assignments.product.dto.request.CreateProductRequest;
import org.assignments.product.dto.request.UpdateProductRequest;
import org.assignments.product.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    ProductResponse getProductById(Long id);

    ProductResponse getProductByProductCode(String productCode);

    List<ProductResponse> getAllProducts();

    List<ProductResponse> getProductsByCategory(Long categoryId);

    List<ProductResponse> getProductsByVendor(Long vendorId);

    List<ProductResponse> searchProducts(String query);

    List<ProductResponse> getLowStockProducts();

    ProductResponse toggleProductStatus(Long id, boolean active);

    void softDeleteProduct(Long id);

    boolean checkAndReserveStock(String productCode, int requestedQuantity);
}