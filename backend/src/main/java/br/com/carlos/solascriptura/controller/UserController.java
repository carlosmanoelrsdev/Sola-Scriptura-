package br.com.carlos.solascriptura.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.carlos.solascriptura.dto.user.UpdateUserRequestDTO;
import br.com.carlos.solascriptura.dto.user.UserResponseDTO;
import br.com.carlos.solascriptura.usecases.auth.DeleteAccountUseCase;
import br.com.carlos.solascriptura.usecases.user.GetCurrentUserUseCase;
import br.com.carlos.solascriptura.usecases.user.UpdateUserUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final DeleteAccountUseCase deleteAccountUseCase;
	private final GetCurrentUserUseCase getCurrentUserUseCase;
	private final UpdateUserUseCase updateUserUseCase;

	public UserController(
			DeleteAccountUseCase deleteAccountUseCase,
			GetCurrentUserUseCase getCurrentUserUseCase,
			UpdateUserUseCase updateUserUseCase) {
		this.deleteAccountUseCase = deleteAccountUseCase;
		this.getCurrentUserUseCase = getCurrentUserUseCase;
		this.updateUserUseCase = updateUserUseCase;
	}

	@GetMapping("/me")
	public ResponseEntity<UserResponseDTO> getMe(Authentication authentication) {
		return ResponseEntity.ok(getCurrentUserUseCase.execute(authentication.getName()));
	}

	@PutMapping("/me")
	public ResponseEntity<UserResponseDTO> updateMe(
			Authentication authentication,
			@Valid @RequestBody UpdateUserRequestDTO request) {
		return ResponseEntity.ok(updateUserUseCase.execute(authentication.getName(), request));
	}

	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteMe(Authentication authentication) {
		deleteAccountUseCase.execute(authentication.getName());
		return ResponseEntity.noContent().build();
	}
}
