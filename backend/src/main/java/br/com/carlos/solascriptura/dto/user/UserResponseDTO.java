package br.com.carlos.solascriptura.dto.user;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponseDTO(
		UUID id,
		String name,
		String email,
		String role,
		OffsetDateTime createdAt,
		OffsetDateTime updatedAt) {
}
