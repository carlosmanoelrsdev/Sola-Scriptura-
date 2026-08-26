package br.com.carlos.solascriptura.dto.bible;

public record BibleVerseResponseDTO(
		String reference,
		String version,
		BibleBookResponseDTO book,
		int chapter,
		int verse,
		String text) {
}
