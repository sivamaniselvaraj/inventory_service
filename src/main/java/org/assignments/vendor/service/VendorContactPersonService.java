package org.assignments.vendor.service;

import org.assignments.vendor.dto.request.CreateContactPersonRequest;
import org.assignments.vendor.dto.request.UpdateContactPersonRequest;
import org.assignments.vendor.dto.response.ContactPersonResponse;
import org.assignments.vendor.enums.ContactRole;

import java.util.List;

public interface VendorContactPersonService {

    /** Add a new contact person to a vendor */
    ContactPersonResponse addContact(Long vendorId, CreateContactPersonRequest request);

    /** Update an existing contact */
    ContactPersonResponse updateContact(Long vendorId, Long contactId, UpdateContactPersonRequest request);

    /** Get one contact by id */
    ContactPersonResponse getContactById(Long vendorId, Long contactId);

    /** All active contacts for a vendor */
    List<ContactPersonResponse> getContactsForVendor(Long vendorId);

    /** All contacts for a vendor filtered by role */
    List<ContactPersonResponse> getContactsByRole(Long vendorId, ContactRole role);

    /** Preferred contact for a vendor */
    ContactPersonResponse getPreferredContact(Long vendorId);

    /** Promote a contact to preferred (clears others) */
    ContactPersonResponse setPreferredContact(Long vendorId, Long contactId);

    /** Enable / disable a contact */
    ContactPersonResponse toggleContactStatus(Long vendorId, Long contactId, boolean active);

    /** Soft-delete a contact */
    void removeContact(Long vendorId, Long contactId);
}
