package br.com.carlos.solascriptura.dto.bible;

public record BibleVersionResponseDTO(
		String code,
		String copyright,
		String permissions,
		String language) {
}
