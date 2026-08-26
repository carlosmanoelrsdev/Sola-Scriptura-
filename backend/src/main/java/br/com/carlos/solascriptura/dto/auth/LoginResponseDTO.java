package br.com.carlos.solascriptura.dto.auth;

public record LoginResponseDTO(
		String token,
		String tokenType,
		long expiresIn,
		AuthUserResponseDTO user) {
}
