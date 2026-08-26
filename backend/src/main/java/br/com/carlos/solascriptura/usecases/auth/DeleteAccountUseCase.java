package br.com.carlos.solascriptura.usecases.auth;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.exceptions.ResourceNotFoundException;
import br.com.carlos.solascriptura.repository.ReadingRepository;
import br.com.carlos.solascriptura.repository.UserRepository;

@Component
public class DeleteAccountUseCase {

	private final UserRepository userRepository;
	private final ReadingRepository readingRepository;

	public DeleteAccountUseCase(UserRepository userRepository, ReadingRepository readingRepository) {
		this.userRepository = userRepository;
		this.readingRepository = readingRepository;
	}

	@Transactional
	public void execute(String authenticatedEmail) {
		User user = userRepository.findByEmailIgnoreCase(authenticatedEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		readingRepository.deleteByUser(user);
		userRepository.delete(user);
	}
}
