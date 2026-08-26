package br.com.carlos.solascriptura.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.carlos.solascriptura.usecases.auth.DeleteAccountUseCase;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final DeleteAccountUseCase deleteAccountUseCase;

	public UserController(DeleteAccountUseCase deleteAccountUseCase) {
		this.deleteAccountUseCase = deleteAccountUseCase;
	}

	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteMe(Authentication authentication) {
		deleteAccountUseCase.execute(authentication.getName());
		return ResponseEntity.noContent().build();
	}
}
