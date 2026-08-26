package br.com.carlos.solascriptura.usecases.user;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.carlos.solascriptura.dto.user.UserResponseDTO;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.exceptions.ResourceNotFoundException;
import br.com.carlos.solascriptura.repository.UserRepository;

@Component
public class GetCurrentUserUseCase {

	private final UserRepository userRepository;

	public GetCurrentUserUseCase(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public UserResponseDTO execute(String authenticatedEmail) {
		User user = userRepository.findByEmailIgnoreCase(authenticatedEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return UserMapper.toResponse(user);
	}
}
