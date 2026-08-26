package br.com.carlos.solascriptura.usecases.user;

import br.com.carlos.solascriptura.dto.user.UserResponseDTO;
import br.com.carlos.solascriptura.entities.User;

final class UserMapper {

	private UserMapper() {
	}

	static UserResponseDTO toResponse(User user) {
		return new UserResponseDTO(
				user.getId(),
				user.getName(),
				user.getEmail(),
				user.getRole().name(),
				user.getCreatedAt(),
				user.getUpdatedAt());
	}
}
