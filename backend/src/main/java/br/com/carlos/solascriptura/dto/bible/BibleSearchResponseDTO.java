package br.com.carlos.solascriptura.dto.bible;

import java.util.List;

public record BibleSearchResponseDTO(
		String query,
		String version,
		int limit,
		int offset,
		List<BibleSearchResultDTO> results) {
}
