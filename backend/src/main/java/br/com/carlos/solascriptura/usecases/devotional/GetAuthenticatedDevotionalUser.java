package br.com.carlos.solascriptura.usecases.devotional;

import org.springframework.stereotype.Component;

import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.exceptions.ResourceNotFoundException;
import br.com.carlos.solascriptura.repository.UserRepository;

@Component
class GetAuthenticatedDevotionalUser {

	private final UserRepository userRepository;

	GetAuthenticatedDevotionalUser(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	User execute(String authenticatedEmail) {
		return userRepository.findByEmailIgnoreCase(authenticatedEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}
}
