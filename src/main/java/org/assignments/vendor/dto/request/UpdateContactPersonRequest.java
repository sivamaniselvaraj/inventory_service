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
public class UpdateContactPersonRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 200)
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

    @Size(max = 150)
    private String designation;

    @Size(max = 100)
    private String department;

    private boolean preferred;

    @Size(max = 1000)
    private String notes;

    private String updatedBy;
}