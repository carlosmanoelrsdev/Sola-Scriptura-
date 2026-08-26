package br.com.carlos.solascriptura.dto.bible;

import java.util.List;

public record BibleChapterResponseDTO(
		String reference,
		String version,
		BibleBookResponseDTO book,
		BibleChapterInfoDTO chapter,
		List<BibleVerseResponseDTO> verses) {
}
