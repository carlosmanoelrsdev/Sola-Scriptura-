package br.com.carlos.solascriptura.dto.devotional;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DevotionalReferenceRequestDTO(
		@NotBlank(message = "Book is required")
		@Size(max = 80, message = "Book must have at most 80 characters")
		String book,
		@Min(value = 1, message = "Chapter must be greater than zero")
		@Max(value = 150, message = "Chapter must be less than or equal to 150")
		int chapter,
		@Min(value = 1, message = "Start verse must be greater than zero")
		@Max(value = 200, message = "Start verse must be less than or equal to 200")
		int startVerse,
		@Min(value = 1, message = "End verse must be greater than zero")
		@Max(value = 200, message = "End verse must be less than or equal to 200")
		Integer endVerse,
		@NotBlank(message = "Version is required")
		@Size(max = 20, message = "Version must have at most 20 characters")
		String version) {
}
