package br.com.carlos.solascriptura.usecases.reading;

import org.springframework.stereotype.Component;

import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.exceptions.ResourceNotFoundException;
import br.com.carlos.solascriptura.repository.UserRepository;

@Component
class GetAuthenticatedUser {

	private final UserRepository userRepository;

	GetAuthenticatedUser(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	User execute(String authenticatedEmail) {
		return userRepository.findByEmailIgnoreCase(authenticatedEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}
}
