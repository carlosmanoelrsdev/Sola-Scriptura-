package br.com.carlos.solascriptura.usecases.devotional;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.carlos.solascriptura.entities.Devotional;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.exceptions.ResourceNotFoundException;
import br.com.carlos.solascriptura.repository.DevotionalRepository;

@Component
public class DeleteDevotionalUseCase {

	private final DevotionalRepository devotionalRepository;
	private final GetAuthenticatedDevotionalUser getAuthenticatedUser;

	public DeleteDevotionalUseCase(
			DevotionalRepository devotionalRepository,
			GetAuthenticatedDevotionalUser getAuthenticatedUser) {
		this.devotionalRepository = devotionalRepository;
		this.getAuthenticatedUser = getAuthenticatedUser;
	}

	@Transactional
	public void execute(String authenticatedEmail, UUID devotionalId) {
		User user = getAuthenticatedUser.execute(authenticatedEmail);
		Devotional devotional = devotionalRepository.findByIdAndUser(devotionalId, user)
				.orElseThrow(() -> new ResourceNotFoundException("Devotional not found"));

		devotionalRepository.delete(devotional);
	}
}
