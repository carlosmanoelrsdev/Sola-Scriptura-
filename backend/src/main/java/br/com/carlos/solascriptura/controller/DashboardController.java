package br.com.carlos.solascriptura.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.carlos.solascriptura.dto.dashboard.DashboardResponseDTO;
import br.com.carlos.solascriptura.usecases.dashboard.GetDashboardUseCase;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

	private final GetDashboardUseCase getDashboardUseCase;

	public DashboardController(GetDashboardUseCase getDashboardUseCase) {
		this.getDashboardUseCase = getDashboardUseCase;
	}

	@GetMapping
	public ResponseEntity<DashboardResponseDTO> getDashboard(Authentication authentication) {
		return ResponseEntity.ok(getDashboardUseCase.execute(authentication.getName()));
	}
}
