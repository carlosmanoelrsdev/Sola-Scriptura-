package br.com.carlos.solascriptura.usecases.devotional;

import java.util.List;

import br.com.carlos.solascriptura.dto.devotional.DevotionalReferenceRequestDTO;
import br.com.carlos.solascriptura.dto.devotional.DevotionalReferenceResponseDTO;
import br.com.carlos.solascriptura.dto.devotional.DevotionalResponseDTO;
import br.com.carlos.solascriptura.entities.Devotional;
import br.com.carlos.solascriptura.entities.DevotionalReference;
import br.com.carlos.solascriptura.exceptions.BusinessException;

final class DevotionalMapper {

	private DevotionalMapper() {
	}

	static DevotionalResponseDTO toResponse(Devotional devotional) {
		return new DevotionalResponseDTO(
				devotional.getId(),
				devotional.getTitle(),
				devotional.getContent(),
				devotional.getCreatedAt(),
				devotional.getUpdatedAt(),
				devotional.getReferences().stream().map(DevotionalMapper::toReferenceResponse).toList());
	}

	static List<DevotionalReference> toReferences(List<DevotionalReferenceRequestDTO> requests) {
		if (requests == null) {
			return List.of();
		}

		return requests.stream().map(DevotionalMapper::toReference).toList();
	}

	private static DevotionalReference toReference(DevotionalReferenceRequestDTO request) {
		if (request.endVerse() != null && request.endVerse() < request.startVerse()) {
			throw new BusinessException("End verse must be greater than or equal to start verse");
		}

		DevotionalReference reference = new DevotionalReference();
		reference.setBook(request.book().trim().toLowerCase());
		reference.setChapter(request.chapter());
		reference.setStartVerse(request.startVerse());
		reference.setEndVerse(request.endVerse());
		reference.setVersion(request.version().trim().toUpperCase());
		return reference;
	}

	private static DevotionalReferenceResponseDTO toReferenceResponse(DevotionalReference reference) {
		return new DevotionalReferenceResponseDTO(
				reference.getId(),
				reference.getBook(),
				reference.getChapter(),
				reference.getStartVerse(),
				reference.getEndVerse(),
				reference.getVersion());
	}
}
