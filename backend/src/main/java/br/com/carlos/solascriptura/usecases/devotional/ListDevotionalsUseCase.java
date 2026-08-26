package br.com.carlos.solascriptura.usecases.devotional;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.carlos.solascriptura.dto.devotional.DevotionalResponseDTO;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.repository.DevotionalRepository;

@Component
public class ListDevotionalsUseCase {

	private final DevotionalRepository devotionalRepository;
	private final GetAuthenticatedDevotionalUser getAuthenticatedUser;

	public ListDevotionalsUseCase(
			DevotionalRepository devotionalRepository,
			GetAuthenticatedDevotionalUser getAuthenticatedUser) {
		this.devotionalRepository = devotionalRepository;
		this.getAuthenticatedUser = getAuthenticatedUser;
	}

	@Transactional(readOnly = true)
	public List<DevotionalResponseDTO> execute(String authenticatedEmail) {
		User user = getAuthenticatedUser.execute(authenticatedEmail);
		return devotionalRepository.findByUserOrderByUpdatedAtDesc(user)
				.stream()
				.map(DevotionalMapper::toResponse)
				.toList();
	}
}
