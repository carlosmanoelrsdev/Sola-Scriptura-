package br.com.carlos.solascriptura.usecases.devotional;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.carlos.solascriptura.dto.devotional.DevotionalResponseDTO;
import br.com.carlos.solascriptura.dto.devotional.UpdateDevotionalRequestDTO;
import br.com.carlos.solascriptura.entities.Devotional;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.exceptions.ResourceNotFoundException;
import br.com.carlos.solascriptura.repository.DevotionalRepository;

@Component
public class UpdateDevotionalUseCase {

	private final DevotionalRepository devotionalRepository;
	private final GetAuthenticatedDevotionalUser getAuthenticatedUser;

	public UpdateDevotionalUseCase(
			DevotionalRepository devotionalRepository,
			GetAuthenticatedDevotionalUser getAuthenticatedUser) {
		this.devotionalRepository = devotionalRepository;
		this.getAuthenticatedUser = getAuthenticatedUser;
	}

	@Transactional
	public DevotionalResponseDTO execute(String authenticatedEmail, UUID devotionalId, UpdateDevotionalRequestDTO request) {
		User user = getAuthenticatedUser.execute(authenticatedEmail);
		Devotional devotional = devotionalRepository.findByIdAndUser(devotionalId, user)
				.orElseThrow(() -> new ResourceNotFoundException("Devotional not found"));

		devotional.setTitle(request.title().trim());
		devotional.setContent(request.content().trim());
		devotional.replaceReferences(DevotionalMapper.toReferences(request.references()));

		return DevotionalMapper.toResponse(devotional);
	}
}
