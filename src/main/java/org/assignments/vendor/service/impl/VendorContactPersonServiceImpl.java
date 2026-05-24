package org.assignments.vendor.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assignments.inventory.exception.InventoryException;
import org.assignments.inventory.exception.ResourceNotFoundException;
import org.assignments.vendor.dto.request.CreateContactPersonRequest;
import org.assignments.vendor.dto.request.UpdateContactPersonRequest;
import org.assignments.vendor.dto.response.ContactPersonResponse;
import org.assignments.vendor.entity.Vendor;
import org.assignments.vendor.entity.VendorContactPerson;
import org.assignments.vendor.enums.ContactRole;
import org.assignments.vendor.repository.VendorContactPersonRepository;
import org.assignments.vendor.repository.VendorRepository;
import org.assignments.vendor.service.VendorContactPersonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorContactPersonServiceImpl implements VendorContactPersonService {

    private final VendorContactPersonRepository contactPersonRepository;
    private final VendorRepository vendorRepository;

    // ── Add ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ContactPersonResponse addContact(Long vendorId, CreateContactPersonRequest request) {
        log.info("Adding contact '{}' with role {} to vendor {}", request.getFullName(), request.getRole(), vendorId);

        Vendor vendor = vendorRepository.findActiveById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", vendorId));

        // If marking as preferred, clear the existing preferred flag first
        if (request.isPreferred()) {
            clearPreferredFlag(vendorId, null);
        }

        VendorContactPerson contact = VendorContactPerson.builder()
                .vendor(vendor)
                .fullName(request.getFullName())
                .role(request.getRole())
                .email(request.getEmail())
                .phone(request.getPhone())
                .alternatePhone(request.getAlternatePhone())
                .designation(request.getDesignation())
                .department(request.getDepartment())
                .preferred(request.isPreferred())
                .notes(request.getNotes())
                .createdBy(request.getCreatedBy())
                .build();

        VendorContactPerson saved = contactPersonRepository.save(contact);
        log.info("Contact person id {} added to vendor {}", saved.getId(), vendorId);
        return toResponse(saved);
    }

    // ── Update ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public ContactPersonResponse updateContact(Long vendorId, Long contactId,
                                               UpdateContactPersonRequest request) {
        log.info("Updating contact {} for vendor {}", contactId, vendorId);

        VendorContactPerson contact = findActiveContact(vendorId, contactId);

        if (request.isPreferred() && !contact.isPreferred()) {
            clearPreferredFlag(vendorId, contactId);
        }

        contact.setFullName(request.getFullName());
        contact.setRole(request.getRole());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setAlternatePhone(request.getAlternatePhone());
        contact.setDesignation(request.getDesignation());
        contact.setDepartment(request.getDepartment());
        contact.setPreferred(request.isPreferred());
        contact.setNotes(request.getNotes());
        contact.setUpdatedBy(request.getUpdatedBy());
        contact.setUpdatedAt(LocalDateTime.now());

        return toResponse(contactPersonRepository.save(contact));
    }

    // ── Reads ───────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ContactPersonResponse getContactById(Long vendorId, Long contactId) {
        return toResponse(findActiveContact(vendorId, contactId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactPersonResponse> getContactsForVendor(Long vendorId) {
        log.debug("Fetching all contacts for vendor {}", vendorId);
        return contactPersonRepository.findActiveByVendorId(vendorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactPersonResponse> getContactsByRole(Long vendorId, ContactRole role) {
        log.debug("Fetching contacts for vendor {} with role {}", vendorId, role);
        return contactPersonRepository.findByVendorIdAndRole(vendorId, role).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ContactPersonResponse getPreferredContact(Long vendorId) {
        return contactPersonRepository.findPreferredByVendorId(vendorId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Preferred contact", "vendorId", vendorId));
    }

    // ── Business operations ─────────────────────────────────────────

    @Override
    @Transactional
    public ContactPersonResponse setPreferredContact(Long vendorId, Long contactId) {
        log.info("Setting contact {} as preferred for vendor {}", contactId, vendorId);
        clearPreferredFlag(vendorId, contactId);
        VendorContactPerson contact = findActiveContact(vendorId, contactId);
        contact.setPreferred(true);
        contact.setUpdatedAt(LocalDateTime.now());
        return toResponse(contactPersonRepository.save(contact));
    }

    @Override
    @Transactional
    public ContactPersonResponse toggleContactStatus(Long vendorId, Long contactId, boolean active) {
        log.info("Setting contact {} active={} for vendor {}", contactId, active, vendorId);
        VendorContactPerson contact = findActiveContact(vendorId, contactId);

        if (!active && contact.isPreferred()) {
            long activeCount = contactPersonRepository.findActiveByVendorId(vendorId).size();
            if (activeCount <= 1) {
                throw new InventoryException(
                        "Cannot deactivate the sole preferred contact for vendor " + vendorId
                                + ". Assign another preferred contact first.");
            }
            contact.setPreferred(false);
        }

        contact.setActive(active);
        contact.setUpdatedAt(LocalDateTime.now());
        return toResponse(contactPersonRepository.save(contact));
    }

    @Override
    @Transactional
    public void removeContact(Long vendorId, Long contactId) {
        log.info("Soft deleting contact {} from vendor {}", contactId, vendorId);
        VendorContactPerson contact = findActiveContact(vendorId, contactId);

        if (contact.isPreferred()) {
            long activeCount = contactPersonRepository.findActiveByVendorId(vendorId).size();
            if (activeCount <= 1) {
                throw new InventoryException(
                        "Cannot delete the sole preferred contact for vendor " + vendorId
                                + ". Assign another preferred contact before deleting this one.");
            }
        }

        contact.setDeleted(true);
        contact.setActive(false);
        contact.setUpdatedAt(LocalDateTime.now());
        contactPersonRepository.save(contact);
        log.info("Contact {} soft deleted from vendor {}", contactId, vendorId);
    }

    // ── Helpers ─────────────────────────────────────────────────────

    private VendorContactPerson findActiveContact(Long vendorId, Long contactId) {
        return contactPersonRepository.findByIdAndVendorId(contactId, vendorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "VendorContactPerson", "id", contactId));
    }

    /** Clears preferred flag on all contacts for vendorId except excludeId */
    private void clearPreferredFlag(Long vendorId, Long excludeId) {
        contactPersonRepository.findActiveByVendorId(vendorId).stream()
                .filter(c -> c.isPreferred() && !c.getId().equals(excludeId))
                .forEach(c -> {
                    c.setPreferred(false);
                    c.setUpdatedAt(LocalDateTime.now());
                    contactPersonRepository.save(c);
                });
    }

    private ContactPersonResponse toResponse(VendorContactPerson c) {
        return ContactPersonResponse.builder()
                .id(c.getId())
                .vendorId(c.getVendor().getId())
                .vendorCode(c.getVendor().getVendorCode())
                .vendorName(c.getVendor().getName())
                .fullName(c.getFullName())
                .role(c.getRole())
                .email(c.getEmail())
                .phone(c.getPhone())
                .alternatePhone(c.getAlternatePhone())
                .designation(c.getDesignation())
                .department(c.getDepartment())
                .preferred(c.isPreferred())
                .notes(c.getNotes())
                .active(c.isActive())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .createdBy(c.getCreatedBy())
                .updatedBy(c.getUpdatedBy())
                .build();
    }
}