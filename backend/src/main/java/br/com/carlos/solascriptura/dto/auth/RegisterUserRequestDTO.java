package br.com.carlos.solascriptura.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequestDTO(
		@NotBlank(message = "Name is required")
		@Size(min = 2, max = 120, message = "Name must be between 2 and 120 characters")
		String name,
		@NotBlank(message = "Email is required")
		@Email(message = "Email is invalid")
		@Size(max = 180, message = "Email must have at most 180 characters")
		String email,
		@NotBlank(message = "Password is required")
		@Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
		String password) {
}
