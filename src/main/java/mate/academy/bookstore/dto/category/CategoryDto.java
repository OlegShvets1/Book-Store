package mate.academy.bookstore.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryDto(
        @NotBlank
        String name,
        @NotBlank
        String description
) {
}
