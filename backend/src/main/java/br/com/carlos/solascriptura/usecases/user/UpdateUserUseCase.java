package br.com.carlos.solascriptura.usecases.user;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.carlos.solascriptura.dto.user.UpdateUserRequestDTO;
import br.com.carlos.solascriptura.dto.user.UserResponseDTO;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.exceptions.BusinessException;
import br.com.carlos.solascriptura.exceptions.ResourceNotFoundException;
import br.com.carlos.solascriptura.repository.UserRepository;

@Component
public class UpdateUserUseCase {

	private final UserRepository userRepository;

	public UpdateUserUseCase(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public UserResponseDTO execute(String authenticatedEmail, UpdateUserRequestDTO request) {
		User user = userRepository.findByEmailIgnoreCase(authenticatedEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		String normalizedEmail = request.email().trim().toLowerCase();

		if (!user.getEmail().equalsIgnoreCase(normalizedEmail)
				&& userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
			throw new BusinessException("Email is already registered");
		}

		user.setName(request.name().trim());
		user.setEmail(normalizedEmail);

		return UserMapper.toResponse(userRepository.save(user));
	}
}
