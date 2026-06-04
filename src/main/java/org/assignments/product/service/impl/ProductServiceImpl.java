package org.assignments.product.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assignments.product.dto.request.CreateProductRequest;
import org.assignments.product.dto.request.UpdateProductRequest;
import org.assignments.product.dto.response.ProductResponse;
import org.assignments.product.entity.Product;
import org.assignments.category.entity.Category;
import org.assignments.vendor.entity.Vendor;
import org.assignments.inventory.exception.DuplicateResourceException;
import org.assignments.inventory.exception.ResourceNotFoundException;
import org.assignments.category.repository.CategoryRepository;
import org.assignments.product.repository.ProductRepository;
import org.assignments.vendor.entity.VendorContactPerson;
import org.assignments.vendor.repository.VendorRepository;
import org.assignments.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating product with Product code: {}", request.getProductCode());

        if (productRepository.existsByProductCodeIgnoreCase(request.getProductCode())) {
            throw new DuplicateResourceException("Product", "productCode", request.getProductCode());
        }

        Category category = categoryRepository.findActiveById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "id", request.getCategoryId()));

        Product product = Product.builder()
                .productCode(request.getProductCode())
                .name(request.getName())
                .description(request.getDescription())
                .unitPrice(request.getUnitPrice())
                .sellingPrice(request.getSellingPrice())
                .purchasePrice(request.getPurchasingPrice())
                .unitsAvailable(request.getUnitsAvailable())
                .barCodeNumber(request.getBarCodeValue())
                .unitOfMeasure(request.getUnitOfMeasure())
                .manufacturedBy(request.getManufacturedBy())
                .manufacturedOn(request.getManufacturedOn())
                .expiryDate(request.getExpiryDate())
                .shelfLifeDays(request.getShelfLifeDays())
                .batchNumber(request.getBatchNumber())
                .reorderLevel(request.getReorderLevel() != null ? request.getReorderLevel() : 0)
                .maxStockLevel(request.getMaxStockLevel())
                .category(category)
                .createdBy(request.getCreatedBy())
                .build();

        Product saved = productRepository.save(product);
        log.info("Product created successfully with id: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product with id: {}", id);

        Product product = productRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (productRepository.existsByCodeExcluding(product.getProductCode(), id)) {
            throw new DuplicateResourceException("Product", "productCode", product.getProductCode());
        }

        Category category = categoryRepository.findActiveById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory", "id", request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setUnitPrice(request.getUnitPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setPurchasePrice(request.getPurchasingPrice());
        product.setUnitsAvailable(request.getUnitsAvailable());
        product.setUnitOfMeasure(request.getUnitOfMeasure());
        product.setBarCodeNumber(request.getBarCodeValue());
        product.setManufacturedBy(request.getManufacturedBy());
        product.setManufacturedOn(request.getManufacturedOn());
        product.setExpiryDate(request.getExpiryDate());
        product.setShelfLifeDays(request.getShelfLifeDays());
        product.setBatchNumber(request.getBatchNumber());
        product.setReorderLevel(request.getReorderLevel());
        product.setMaxStockLevel(request.getMaxStockLevel());
        product.setCategory(category);
        product.setUpdatedBy(request.getUpdatedBy());

        Product updated = productRepository.save(product);
        log.info("Product updated successfully with id: {}", updated.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.debug("Fetching product by id: {}", id);
        Product product = productRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductByProductCode(String productCode) {
        log.debug("Fetching product by Product code: {}", productCode);
        Product product = productRepository.findByCode(productCode)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productCode", productCode));
        return toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.debug("Fetching all active products");
        return productRepository.findAllActive().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        log.debug("Fetching products by category id: {}", categoryId);
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByVendor(Long vendorId) {
        log.debug("Fetching products by vendor id: {}", vendorId);
        return productRepository.findByVendorId(vendorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String query) {
        log.debug("Searching products with query: {}", query);
        return productRepository.searchProducts(query).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts() {
        log.debug("Fetching low stock products");
        return productRepository.findLowStockProducts().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponse toggleProductStatus(Long id, boolean active) {
        log.info("Setting product id: {} active={}", id, active);
        Product product = productRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setActive(active);
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void softDeleteProduct(Long id) {
        log.info("Soft deleting product with id: {}", id);
        productRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        int rows = productRepository.softDeleteById(id);
        if (rows == 0) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        log.info("Product soft deleted with id: {}", id);
    }

    @Override
    @Transactional
    public boolean checkAndReserveStock(String productCode, int requestedQuantity) {
        log.info("Checking stock for product code: {}, quantity: {}", productCode, requestedQuantity);

        Product product = productRepository.findByCode(productCode).orElse(null);

        if (product == null) {
            log.warn("Product not found for Product code: {}", productCode);
            return false;
        }
        if (!product.isActive() || product.isDeleted()) {
            log.warn("Product is inactive or deleted: {}", productCode);
            return false;
        }
        if (product.isExpired()) {
            log.warn("Product is expired: {}", productCode);
            return false;
        }
        if (!product.hasStock(requestedQuantity)) {
            log.warn("Insufficient stock for product code: {}. Available: {}, Requested: {}",
                    productCode, product.getUnitsAvailable(), requestedQuantity);
            return false;
        }

        // Reserve (deduct) stock
        product.setUnitsAvailable(product.getUnitsAvailable() - requestedQuantity);
        productRepository.save(product);
        log.info("Stock reserved for Product code: {}. Remaining units: {}", productCode, product.getUnitsAvailable());
        return true;
    }

    private ProductResponse toResponse(Product product) {
        boolean lowStock = product.getReorderLevel() != null
                && product.getUnitsAvailable() != null
                && product.getUnitsAvailable() <= product.getReorderLevel();

        return ProductResponse.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .description(product.getDescription())
                .unitPrice(product.getUnitPrice())
                .sellingPrice(product.getSellingPrice())
                .purchasingPrice(product.getPurchasePrice())
                .unitsAvailable(product.getUnitsAvailable())
                .barCodeValue(product.getBarCodeNumber())
                .unitOfMeasure(product.getUnitOfMeasure())
                .manufacturedBy(product.getManufacturedBy())
                .manufacturedOn(product.getManufacturedOn())
                .expiryDate(product.getExpiryDate())
                .shelfLifeDays(product.getShelfLifeDays())
                .batchNumber(product.getBatchNumber())
                .reorderLevel(product.getReorderLevel())
                .maxStockLevel(product.getMaxStockLevel())
                .active(product.isActive())
                .expired(product.isExpired())
                .lowStock(lowStock)
                .category(ProductResponse.CategorySummary.builder()
                        .id(product.getCategory().getId())
                        .code(product.getCategory().getCode())
                        .name(product.getCategory().getName())
                        .build())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .createdBy(product.getCreatedBy())
                .updatedBy(product.getUpdatedBy())
                .build();
    }


}