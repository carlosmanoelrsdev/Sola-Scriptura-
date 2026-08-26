package br.com.carlos.solascriptura.usecases.devotional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.carlos.solascriptura.dto.devotional.CreateDevotionalRequestDTO;
import br.com.carlos.solascriptura.dto.devotional.DevotionalResponseDTO;
import br.com.carlos.solascriptura.entities.Devotional;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.repository.DevotionalRepository;

@Component
public class CreateDevotionalUseCase {

	private final DevotionalRepository devotionalRepository;
	private final GetAuthenticatedDevotionalUser getAuthenticatedUser;

	public CreateDevotionalUseCase(
			DevotionalRepository devotionalRepository,
			GetAuthenticatedDevotionalUser getAuthenticatedUser) {
		this.devotionalRepository = devotionalRepository;
		this.getAuthenticatedUser = getAuthenticatedUser;
	}

	@Transactional
	public DevotionalResponseDTO execute(String authenticatedEmail, CreateDevotionalRequestDTO request) {
		User user = getAuthenticatedUser.execute(authenticatedEmail);

		Devotional devotional = new Devotional();
		devotional.setUser(user);
		devotional.setTitle(request.title().trim());
		devotional.setContent(request.content().trim());
		devotional.replaceReferences(DevotionalMapper.toReferences(request.references()));

		return DevotionalMapper.toResponse(devotionalRepository.save(devotional));
	}
}
