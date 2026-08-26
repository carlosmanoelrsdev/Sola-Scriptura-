package br.com.carlos.solascriptura.usecases.auth;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.exceptions.ResourceNotFoundException;
import br.com.carlos.solascriptura.repository.UserRepository;

@Component
public class DeleteAccountUseCase {

	private final UserRepository userRepository;

	public DeleteAccountUseCase(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public void execute(String authenticatedEmail) {
		User user = userRepository.findByEmailIgnoreCase(authenticatedEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		userRepository.delete(user);
	}
}
