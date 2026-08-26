package br.com.carlos.solascriptura.usecases.reading;

import br.com.carlos.solascriptura.dto.reading.ReadingResponseDTO;
import br.com.carlos.solascriptura.entities.Reading;

final class ReadingMapper {

	private ReadingMapper() {
	}

	static ReadingResponseDTO toResponse(Reading reading) {
		return new ReadingResponseDTO(
				reading.getId(),
				reading.getVersion(),
				reading.getBook(),
				reading.getChapter(),
				reading.getReadAt());
	}
}
