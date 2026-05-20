package org.assignments.vendor.service;

import org.assignments.vendor.dto.request.CreateVendorRequest;
import org.assignments.vendor.dto.request.UpdateVendorRequest;
import org.assignments.vendor.dto.response.VendorResponse;

import java.util.List;

public interface VendorService {

    VendorResponse createVendor(CreateVendorRequest request);

    VendorResponse updateVendor(Long id, UpdateVendorRequest request);

    VendorResponse getVendorById(Long id);

    VendorResponse getVendorByCode(String vendorCode);

    List<VendorResponse> getAllVendors();

    List<VendorResponse> searchVendors(String name);

    VendorResponse toggleVendorStatus(Long id, boolean active);

    void softDeleteVendor(Long id);
}