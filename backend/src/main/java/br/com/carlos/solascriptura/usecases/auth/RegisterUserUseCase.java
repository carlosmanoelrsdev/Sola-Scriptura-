package br.com.carlos.solascriptura.usecases.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.carlos.solascriptura.dto.auth.RegisterUserRequestDTO;
import br.com.carlos.solascriptura.dto.auth.RegisterUserResponseDTO;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.entities.UserRole;
import br.com.carlos.solascriptura.exceptions.BusinessException;
import br.com.carlos.solascriptura.repository.UserRepository;

@Component
public class RegisterUserUseCase {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public RegisterUserResponseDTO execute(RegisterUserRequestDTO request) {
		String normalizedEmail = request.email().trim().toLowerCase();

		if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
			throw new BusinessException("Email is already registered");
		}

		User user = new User();
		user.setName(request.name().trim());
		user.setEmail(normalizedEmail);
		user.setPassword(passwordEncoder.encode(request.password()));
		user.setRole(UserRole.USER);

		User savedUser = userRepository.save(user);

		return new RegisterUserResponseDTO(
				savedUser.getId(),
				savedUser.getName(),
				savedUser.getEmail(),
				savedUser.getRole().name(),
				savedUser.getCreatedAt());
	}
}
