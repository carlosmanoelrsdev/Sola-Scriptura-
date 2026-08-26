package br.com.carlos.solascriptura.dto.reading;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReadingResponseDTO(
		UUID id,
		String version,
		String book,
		int chapter,
		OffsetDateTime readAt) {
}
