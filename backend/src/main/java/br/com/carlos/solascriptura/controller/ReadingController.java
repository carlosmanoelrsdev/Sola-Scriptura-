package br.com.carlos.solascriptura.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.carlos.solascriptura.dto.reading.ReadingProgressResponseDTO;
import br.com.carlos.solascriptura.dto.reading.ReadingResponseDTO;
import br.com.carlos.solascriptura.dto.reading.ReadingStreakResponseDTO;
import br.com.carlos.solascriptura.dto.reading.RegisterReadingRequestDTO;
import br.com.carlos.solascriptura.usecases.reading.GetReadingHistoryUseCase;
import br.com.carlos.solascriptura.usecases.reading.GetReadingProgressUseCase;
import br.com.carlos.solascriptura.usecases.reading.GetReadingStreakUseCase;
import br.com.carlos.solascriptura.usecases.reading.RegisterReadingUseCase;
import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/readings")
public class ReadingController {

	private final RegisterReadingUseCase registerReadingUseCase;
	private final GetReadingHistoryUseCase getReadingHistoryUseCase;
	private final GetReadingStreakUseCase getReadingStreakUseCase;
	private final GetReadingProgressUseCase getReadingProgressUseCase;

	public ReadingController(
			RegisterReadingUseCase registerReadingUseCase,
			GetReadingHistoryUseCase getReadingHistoryUseCase,
			GetReadingStreakUseCase getReadingStreakUseCase,
			GetReadingProgressUseCase getReadingProgressUseCase) {
		this.registerReadingUseCase = registerReadingUseCase;
		this.getReadingHistoryUseCase = getReadingHistoryUseCase;
		this.getReadingStreakUseCase = getReadingStreakUseCase;
		this.getReadingProgressUseCase = getReadingProgressUseCase;
	}

	@PostMapping
	public ResponseEntity<ReadingResponseDTO> register(
			Authentication authentication,
			@Valid @RequestBody RegisterReadingRequestDTO request) {
		ReadingResponseDTO response = registerReadingUseCase.execute(authentication.getName(), request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/history")
	public ResponseEntity<List<ReadingResponseDTO>> history(Authentication authentication) {
		return ResponseEntity.ok(getReadingHistoryUseCase.execute(authentication.getName()));
	}

	@GetMapping("/streak")
	public ResponseEntity<ReadingStreakResponseDTO> streak(Authentication authentication) {
		return ResponseEntity.ok(getReadingStreakUseCase.execute(authentication.getName()));
	}

	@GetMapping("/progress")
	public ResponseEntity<ReadingProgressResponseDTO> progress(Authentication authentication) {
		return ResponseEntity.ok(getReadingProgressUseCase.execute(authentication.getName()));
	}
}
