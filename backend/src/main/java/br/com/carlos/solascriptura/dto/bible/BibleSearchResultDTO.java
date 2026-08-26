package br.com.carlos.solascriptura.dto.bible;

public record BibleSearchResultDTO(
		String reference,
		BibleBookResponseDTO book,
		int chapter,
		int verse,
		String text) {
}
