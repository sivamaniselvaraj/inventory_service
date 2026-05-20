package org.assignments.category.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Long sortOrder;
    private Long parentId;
    private String parentName;
    private boolean active;
    private LocalDateTime createdAt;
    private List<CategoryResponse> subCategories;
}
