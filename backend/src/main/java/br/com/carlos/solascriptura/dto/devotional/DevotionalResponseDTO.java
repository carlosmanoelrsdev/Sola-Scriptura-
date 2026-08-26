package br.com.carlos.solascriptura.dto.devotional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record DevotionalResponseDTO(
		UUID id,
		String title,
		String content,
		OffsetDateTime createdAt,
		OffsetDateTime updatedAt,
		List<DevotionalReferenceResponseDTO> references) {
}
