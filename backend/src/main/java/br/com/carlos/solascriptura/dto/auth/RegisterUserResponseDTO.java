package br.com.carlos.solascriptura.dto.auth;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RegisterUserResponseDTO(
		UUID id,
		String name,
		String email,
		String role,
		OffsetDateTime createdAt) {
}
