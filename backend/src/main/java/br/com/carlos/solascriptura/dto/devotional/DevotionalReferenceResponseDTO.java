package br.com.carlos.solascriptura.dto.devotional;

import java.util.UUID;

public record DevotionalReferenceResponseDTO(
		UUID id,
		String book,
		int chapter,
		int startVerse,
		Integer endVerse,
		String version) {
}
