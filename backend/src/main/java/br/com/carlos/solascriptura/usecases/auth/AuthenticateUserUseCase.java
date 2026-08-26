package br.com.carlos.solascriptura.usecases.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import br.com.carlos.solascriptura.dto.auth.AuthUserResponseDTO;
import br.com.carlos.solascriptura.dto.auth.LoginRequestDTO;
import br.com.carlos.solascriptura.dto.auth.LoginResponseDTO;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.exceptions.ResourceNotFoundException;
import br.com.carlos.solascriptura.repository.UserRepository;
import br.com.carlos.solascriptura.security.JwtService;

@Component
public class AuthenticateUserUseCase {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final JwtService jwtService;

	public AuthenticateUserUseCase(
			AuthenticationManager authenticationManager,
			UserRepository userRepository,
			JwtService jwtService) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.jwtService = jwtService;
	}

	public LoginResponseDTO execute(LoginRequestDTO request) {
		String normalizedEmail = request.email().trim().toLowerCase();

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(normalizedEmail, request.password()));

		User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		String token = jwtService.generateToken(user);

		return new LoginResponseDTO(
				token,
				"Bearer",
				jwtService.getExpiration(),
				new AuthUserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole().name()));
	}
}
