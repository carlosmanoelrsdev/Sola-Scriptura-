package br.com.carlos.solascriptura.dto.bible;

public record BibleBookResponseDTO(
		int id,
		String name,
		String abbrev,
		String testament) {
}
