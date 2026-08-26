package br.com.carlos.solascriptura.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.carlos.solascriptura.dto.devotional.CreateDevotionalRequestDTO;
import br.com.carlos.solascriptura.dto.devotional.DevotionalResponseDTO;
import br.com.carlos.solascriptura.dto.devotional.UpdateDevotionalRequestDTO;
import br.com.carlos.solascriptura.usecases.devotional.CreateDevotionalUseCase;
import br.com.carlos.solascriptura.usecases.devotional.DeleteDevotionalUseCase;
import br.com.carlos.solascriptura.usecases.devotional.GetDevotionalUseCase;
import br.com.carlos.solascriptura.usecases.devotional.ListDevotionalsUseCase;
import br.com.carlos.solascriptura.usecases.devotional.UpdateDevotionalUseCase;
import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/devotionals")
public class DevotionalController {

	private final CreateDevotionalUseCase createDevotionalUseCase;
	private final ListDevotionalsUseCase listDevotionalsUseCase;
	private final GetDevotionalUseCase getDevotionalUseCase;
	private final UpdateDevotionalUseCase updateDevotionalUseCase;
	private final DeleteDevotionalUseCase deleteDevotionalUseCase;

	public DevotionalController(
			CreateDevotionalUseCase createDevotionalUseCase,
			ListDevotionalsUseCase listDevotionalsUseCase,
			GetDevotionalUseCase getDevotionalUseCase,
			UpdateDevotionalUseCase updateDevotionalUseCase,
			DeleteDevotionalUseCase deleteDevotionalUseCase) {
		this.createDevotionalUseCase = createDevotionalUseCase;
		this.listDevotionalsUseCase = listDevotionalsUseCase;
		this.getDevotionalUseCase = getDevotionalUseCase;
		this.updateDevotionalUseCase = updateDevotionalUseCase;
		this.deleteDevotionalUseCase = deleteDevotionalUseCase;
	}

	@PostMapping
	public ResponseEntity<DevotionalResponseDTO> create(
			Authentication authentication,
			@Valid @RequestBody CreateDevotionalRequestDTO request) {
		DevotionalResponseDTO response = createDevotionalUseCase.execute(authentication.getName(), request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<DevotionalResponseDTO>> list(Authentication authentication) {
		return ResponseEntity.ok(listDevotionalsUseCase.execute(authentication.getName()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<DevotionalResponseDTO> get(
			Authentication authentication,
			@PathVariable UUID id) {
		return ResponseEntity.ok(getDevotionalUseCase.execute(authentication.getName(), id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<DevotionalResponseDTO> update(
			Authentication authentication,
			@PathVariable UUID id,
			@Valid @RequestBody UpdateDevotionalRequestDTO request) {
		return ResponseEntity.ok(updateDevotionalUseCase.execute(authentication.getName(), id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(Authentication authentication, @PathVariable UUID id) {
		deleteDevotionalUseCase.execute(authentication.getName(), id);
		return ResponseEntity.noContent().build();
	}
}
