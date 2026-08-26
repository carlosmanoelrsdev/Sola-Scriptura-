package br.com.carlos.solascriptura.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.carlos.solascriptura.dto.auth.LoginRequestDTO;
import br.com.carlos.solascriptura.dto.auth.LoginResponseDTO;
import br.com.carlos.solascriptura.dto.auth.RegisterUserRequestDTO;
import br.com.carlos.solascriptura.dto.auth.RegisterUserResponseDTO;
import br.com.carlos.solascriptura.usecases.auth.AuthenticateUserUseCase;
import br.com.carlos.solascriptura.usecases.auth.RegisterUserUseCase;
import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/auth")
public class AuthController {

	private final RegisterUserUseCase registerUserUseCase;
	private final AuthenticateUserUseCase authenticateUserUseCase;

	public AuthController(RegisterUserUseCase registerUserUseCase, AuthenticateUserUseCase authenticateUserUseCase) {
		this.registerUserUseCase = registerUserUseCase;
		this.authenticateUserUseCase = authenticateUserUseCase;
	}

	@PostMapping("/register")
	public ResponseEntity<RegisterUserResponseDTO> register(@Valid @RequestBody RegisterUserRequestDTO request) {
		RegisterUserResponseDTO response = registerUserUseCase.execute(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
		return ResponseEntity.ok(authenticateUserUseCase.execute(request));
	}
}
