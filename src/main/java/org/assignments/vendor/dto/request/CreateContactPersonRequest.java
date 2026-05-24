package org.assignments.vendor.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.assignments.vendor.enums.ContactRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateContactPersonRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "Full name must not exceed 200 characters")
    private String fullName;

    @NotNull(message = "Role is required")
    private ContactRole role;

    @Email(message = "Invalid email format")
    @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 20)
    private String alternatePhone;

    @Size(max = 150, message = "Designation must not exceed 150 characters")
    private String designation;

    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;

    /** If true, clears the preferred flag from any existing contact for this vendor */
    private boolean preferred;

    @Size(max = 1000)
    private String notes;

    private String createdBy;
}
