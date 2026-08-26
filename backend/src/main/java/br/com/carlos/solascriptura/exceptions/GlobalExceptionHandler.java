package br.com.carlos.solascriptura.exceptions;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
		return build(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
		return build(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new LinkedHashMap<>();

		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
		}

		Map<String, Object> payload = basePayload(HttpStatus.BAD_REQUEST, "Validation failed");
		payload.put("validationErrors", errors);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
	}

	@ExceptionHandler(ExternalProviderException.class)
	public ResponseEntity<Map<String, Object>> handleProvider(ExternalProviderException ex) {
		return build(HttpStatus.BAD_GATEWAY, ex.getMessage());
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Map<String, Object>> handleAuthentication(AuthenticationException ex) {
		return build(HttpStatus.UNAUTHORIZED, "Invalid credentials");
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
		return build(HttpStatus.FORBIDDEN, "Access denied");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "An internal error occurred");
	}

	private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
		Map<String, Object> payload = basePayload(status, message);

		return ResponseEntity.status(status).body(payload);
	}

	private Map<String, Object> basePayload(HttpStatus status, String message) {
		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("timestamp", OffsetDateTime.now().toString());
		payload.put("status", status.value());
		payload.put("error", status.getReasonPhrase());
		payload.put("message", message);
		return payload;
	}
}
