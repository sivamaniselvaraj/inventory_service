package org.assignments.vendor.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.assignments.inventory.entity.BaseEntity;
import org.assignments.vendor.enums.ContactRole;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactPersonResponse {

    private Long id;
    private Long vendorId;
    private String vendorCode;
    private String vendorName;
    private String fullName;
    private ContactRole role;
    private String email;
    private String phone;
    private String alternatePhone;
    private String designation;
    private String department;
    private boolean preferred;
    private String notes;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}