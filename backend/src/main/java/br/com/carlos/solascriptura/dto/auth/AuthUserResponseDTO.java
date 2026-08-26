package br.com.carlos.solascriptura.dto.auth;

import java.util.UUID;

public record AuthUserResponseDTO(
		UUID id,
		String name,
		String email,
		String role) {
}
