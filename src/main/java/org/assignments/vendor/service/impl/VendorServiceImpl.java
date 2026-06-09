package org.assignments.vendor.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assignments.vendor.dto.request.CreateVendorRequest;
import org.assignments.vendor.dto.request.UpdateVendorRequest;
import org.assignments.vendor.dto.response.ContactPersonResponse;
import org.assignments.vendor.dto.response.VendorResponse;
import org.assignments.vendor.entity.Vendor;
import org.assignments.inventory.exception.DuplicateResourceException;
import org.assignments.inventory.exception.ResourceNotFoundException;
import org.assignments.vendor.entity.VendorContactPerson;
import org.assignments.vendor.repository.VendorRepository;
import org.assignments.vendor.service.VendorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    @Override
    @Transactional
    public VendorResponse createVendor(CreateVendorRequest request) {
        log.info("Creating vendor with code: {}", request.getVendorCode());

        if (vendorRepository.existsByVendorCodeIgnoreCase(request.getVendorCode())) {
            throw new DuplicateResourceException("Vendor", "vendorCode", request.getVendorCode());
        }

        Vendor vendor = Vendor.builder()
                .vendorCode(request.getVendorCode())
                .vendorName(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .gstin(request.getGstin())
                .pan(request.getPan())
                .contractStartDate(request.getContractStartDate())
                .contractEndDate(request.getContractEndDate())
                .createdBy(request.getCreatedBy())
                .build();

        Vendor saved = vendorRepository.save(vendor);
        log.info("Vendor created successfully with id: {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public VendorResponse updateVendor(Long id, UpdateVendorRequest request) {
        log.info("Updating vendor with id: {}", id);

        Vendor vendor = vendorRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));

        vendor.setVendorName(request.getName());
        vendor.setEmail(request.getEmail());
        vendor.setPhone(request.getPhone());
        vendor.setAddressLine1(request.getAddressLine1());
        vendor.setAddressLine2(request.getAddressLine2());
        vendor.setCity(request.getCity());
        vendor.setState(request.getState());
        vendor.setCountry(request.getCountry());
        vendor.setPostalCode(request.getPostalCode());
        vendor.setGstin(request.getGstin());
        vendor.setPan(request.getPan());
        vendor.setContractStartDate(request.getContractStartDate());
        vendor.setContractEndDate(request.getContractEndDate());
        vendor.setUpdatedBy(request.getUpdatedBy());

        Vendor updated = vendorRepository.save(vendor);
        log.info("Vendor updated successfully with id: {}", updated.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponse getVendorById(Long id) {
        log.debug("Fetching vendor by id: {}", id);
        Vendor vendor = vendorRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
        return toResponse(vendor);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponse getVendorByCode(String vendorCode) {
        log.debug("Fetching vendor by code: {}", vendorCode);
        Vendor vendor = vendorRepository.findByVendorCode(vendorCode)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "vendorCode", vendorCode));
        return toResponse(vendor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorResponse> getAllVendors() {
        log.debug("Fetching all active vendors");
        return vendorRepository.findAllActive().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorResponse> searchVendors(String name) {
        log.debug("Searching vendors by name: {}", name);
        return vendorRepository.searchByName(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VendorResponse toggleVendorStatus(Long id, boolean active) {
        log.info("Setting vendor id: {} active={}", id, active);
        Vendor vendor = vendorRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
        vendor.setActive(active);
        return toResponse(vendorRepository.save(vendor));
    }

    @Override
    @Transactional
    public void softDeleteVendor(Long id) {
        log.info("Soft deleting vendor with id: {}", id);
        Vendor vendor = vendorRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
        vendor.setDeleted(true);
        vendor.setActive(false);
        vendorRepository.save(vendor);
        log.info("Vendor soft deleted with id: {}", id);
    }

    private VendorResponse toResponse(Vendor vendor) {


        return VendorResponse.builder()
                .id(vendor.getId())
                .vendorCode(vendor.getVendorCode())
                .name(vendor.getVendorName())
                .email(vendor.getEmail())
                .phone(vendor.getPhone())
                .addressLine1(vendor.getAddressLine1())
                .addressLine2(vendor.getAddressLine2())
                .city(vendor.getCity())
                .state(vendor.getState())
                .country(vendor.getCountry())
                .postalCode(vendor.getPostalCode())
                .gstin(vendor.getGstin())
                .pan(vendor.getPan())
                .contactPersons(vendor.getPreferredContact().stream().map(this::toContactResponse).collect(Collectors.toList()))
                .contractStartDate(vendor.getContractStartDate())
                .contractEndDate(vendor.getContractEndDate())
                .active(vendor.isActive())
                .createdAt(vendor.getCreatedAt())
                .updatedAt(vendor.getUpdatedAt())
                .createdBy(vendor.getCreatedBy())
                .updatedBy(vendor.getUpdatedBy())
                .build();
    }

    private ContactPersonResponse toContactResponse(VendorContactPerson c) {
        return ContactPersonResponse.builder()
                .fullName(c.getFullName())
                .role(c.getRole())
                .email(c.getEmail())
                .phone(c.getPhone())
                .alternatePhone(c.getAlternatePhone())
                .designation(c.getDesignation())
                .department(c.getDepartment())
                .preferred(c.isPreferred())
                .notes(c.getNotes())
                .build();
    }
}