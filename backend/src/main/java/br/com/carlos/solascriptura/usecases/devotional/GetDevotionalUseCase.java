package br.com.carlos.solascriptura.usecases.devotional;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.carlos.solascriptura.dto.devotional.DevotionalResponseDTO;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.exceptions.ResourceNotFoundException;
import br.com.carlos.solascriptura.repository.DevotionalRepository;

@Component
public class GetDevotionalUseCase {

	private final DevotionalRepository devotionalRepository;
	private final GetAuthenticatedDevotionalUser getAuthenticatedUser;

	public GetDevotionalUseCase(
			DevotionalRepository devotionalRepository,
			GetAuthenticatedDevotionalUser getAuthenticatedUser) {
		this.devotionalRepository = devotionalRepository;
		this.getAuthenticatedUser = getAuthenticatedUser;
	}

	@Transactional(readOnly = true)
	public DevotionalResponseDTO execute(String authenticatedEmail, UUID devotionalId) {
		User user = getAuthenticatedUser.execute(authenticatedEmail);
		return devotionalRepository.findByIdAndUser(devotionalId, user)
				.map(DevotionalMapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Devotional not found"));
	}
}
