package br.com.carlos.solascriptura.dto.reading;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterReadingRequestDTO(
		@NotBlank(message = "Version is required")
		@Size(max = 20, message = "Version must have at most 20 characters")
		String version,
		@NotBlank(message = "Book is required")
		@Size(max = 80, message = "Book must have at most 80 characters")
		String book,
		@Min(value = 1, message = "Chapter must be greater than zero")
		@Max(value = 150, message = "Chapter must be less than or equal to 150")
		int chapter) {
}
