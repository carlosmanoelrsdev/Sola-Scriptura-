package br.com.carlos.solascriptura.dto.devotional;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDevotionalRequestDTO(
		@NotBlank(message = "Title is required")
		@Size(min = 2, max = 160, message = "Title must be between 2 and 160 characters")
		String title,
		@NotBlank(message = "Content is required")
		@Size(min = 10, max = 10000, message = "Content must be between 10 and 10000 characters")
		String content,
		@Valid
		@Size(max = 20, message = "A devotional can have at most 20 references")
		List<DevotionalReferenceRequestDTO> references) {
}
