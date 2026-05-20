package org.assignments.category.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryRequest {

    @NotBlank(message = "Category code is required")
    @Size(max = 50)
    private String code;

    @NotBlank(message = "Category name is required")
    @Size(max = 200)
    private String name;

    private String description;

    private Long sortOrder;

    private Long parentId;
}
