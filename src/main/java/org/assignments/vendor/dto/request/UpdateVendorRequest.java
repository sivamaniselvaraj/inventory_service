package org.assignments.vendor.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVendorRequest {

    @NotBlank(message = "Vendor name is required")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String phone;

    private String addressLine1;
    private String addressLine2;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 100)
    private String country;

    @Size(max = 20)
    private String postalCode;

    @Size(max = 20)
    private String gstin;

    @Size(max = 20)
    private String pan;

    @Size(max = 150)
    private String contactPerson;

    private LocalDate contractStartDate;
    private LocalDate contractEndDate;

    private String updatedBy;
}