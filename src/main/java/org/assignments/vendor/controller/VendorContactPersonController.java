package org.assignments.vendor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assignments.category.dto.response.ApiResponse;
import org.assignments.vendor.dto.request.CreateContactPersonRequest;
import org.assignments.vendor.dto.request.UpdateContactPersonRequest;
import org.assignments.vendor.dto.response.ContactPersonResponse;
import org.assignments.vendor.enums.ContactRole;
import org.assignments.vendor.service.VendorContactPersonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for vendor contact persons.
 * All endpoints nested under /vendors/{vendorId}/contacts.
 *
 * Endpoints
 * ──────────────────────────────────────────────────────────
 * POST   /vendors/{vendorId}/contacts
 * GET    /vendors/{vendorId}/contacts
 * GET    /vendors/{vendorId}/contacts/preferred
 * GET    /vendors/{vendorId}/contacts/{contactId}
 * GET    /vendors/{vendorId}/contacts?role=SALES
 * PUT    /vendors/{vendorId}/contacts/{contactId}
 * PATCH  /vendors/{vendorId}/contacts/{contactId}/preferred
 * PATCH  /vendors/{vendorId}/contacts/{contactId}/status?active=
 * DELETE /vendors/{vendorId}/contacts/{contactId}
 */
@RestController
@RequestMapping("/vendors/{vendorId}/contacts")
@RequiredArgsConstructor
@Slf4j
public class VendorContactPersonController {

    private final VendorContactPersonService contactPersonService;

    /**
     * Add a contact person to a vendor
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ContactPersonResponse>> addContact(
            @PathVariable Long vendorId,
            @Valid @RequestBody CreateContactPersonRequest request) {
        log.info("REST add contact '{}' to vendor {}", request.getFullName(), vendorId);
        ContactPersonResponse response = contactPersonService.addContact(vendorId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contact person added successfully", response));
    }

    /**
     * List all contacts; optionally filter by role
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactPersonResponse>>> getContacts(
            @PathVariable Long vendorId,
            @RequestParam(required = false) ContactRole role) {
        log.debug("REST get contacts for vendor {}, role={}", vendorId, role);
        List<ContactPersonResponse> contacts = (role != null)
                ? contactPersonService.getContactsByRole(vendorId, role)
                : contactPersonService.getContactsForVendor(vendorId);
        return ResponseEntity.ok(ApiResponse.success(contacts));
    }

    /**
     * Get the preferred contact for a vendor
     */
    @GetMapping("/preferred")
    public ResponseEntity<ApiResponse<ContactPersonResponse>> getPreferredContact(
            @PathVariable Long vendorId) {
        return ResponseEntity.ok(ApiResponse.success(
                contactPersonService.getPreferredContact(vendorId)));
    }

    /**
     * Get a specific contact by id
     */
    @GetMapping("/{contactId}")
    public ResponseEntity<ApiResponse<ContactPersonResponse>> getContact(
            @PathVariable Long vendorId,
            @PathVariable Long contactId) {
        return ResponseEntity.ok(ApiResponse.success(
                contactPersonService.getContactById(vendorId, contactId)));
    }

    /**
     * Update a contact's details
     */
    @PutMapping("/{contactId}")
    public ResponseEntity<ApiResponse<ContactPersonResponse>> updateContact(
            @PathVariable Long vendorId,
            @PathVariable Long contactId,
            @Valid @RequestBody UpdateContactPersonRequest request) {
        log.info("REST update contact {} for vendor {}", contactId, vendorId);
        return ResponseEntity.ok(ApiResponse.success("Contact updated successfully",
                contactPersonService.updateContact(vendorId, contactId, request)));
    }

    /**
     * Promote a contact to preferred (demotes the current preferred)
     */
    @PatchMapping("/{contactId}/preferred")
    public ResponseEntity<ApiResponse<ContactPersonResponse>> setPreferred(
            @PathVariable Long vendorId,
            @PathVariable Long contactId) {
        log.info("REST set contact {} as preferred for vendor {}", contactId, vendorId);
        return ResponseEntity.ok(ApiResponse.success("Preferred contact updated",
                contactPersonService.setPreferredContact(vendorId, contactId)));
    }

    /**
     * Enable or disable a contact
     */
    @PatchMapping("/{contactId}/status")
    public ResponseEntity<ApiResponse<ContactPersonResponse>> toggleStatus(
            @PathVariable Long vendorId,
            @PathVariable Long contactId,
            @RequestParam boolean active) {
        log.info("REST set contact {} active={} for vendor {}", contactId, active, vendorId);
        return ResponseEntity.ok(ApiResponse.success("Contact status updated",
                contactPersonService.toggleContactStatus(vendorId, contactId, active)));
    }

    /**
     * Soft-delete a contact person
     */
    @DeleteMapping("/{contactId}")
    public ResponseEntity<ApiResponse<Void>> removeContact(
            @PathVariable Long vendorId,
            @PathVariable Long contactId) {
        log.info("REST remove contact {} from vendor {}", contactId, vendorId);
        contactPersonService.removeContact(vendorId, contactId);
        return ResponseEntity.ok(ApiResponse.success("Contact person removed", null));
    }
}