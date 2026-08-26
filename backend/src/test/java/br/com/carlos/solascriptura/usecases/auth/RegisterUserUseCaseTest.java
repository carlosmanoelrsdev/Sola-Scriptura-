package br.com.carlos.solascriptura.usecases.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.carlos.solascriptura.dto.auth.RegisterUserRequestDTO;
import br.com.carlos.solascriptura.dto.auth.RegisterUserResponseDTO;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.entities.UserRole;
import br.com.carlos.solascriptura.exceptions.BusinessException;
import br.com.carlos.solascriptura.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private RegisterUserUseCase useCase;

	@Test
	void shouldRegisterUserWithDefaultRoleAndEncodedPassword() {
		RegisterUserRequestDTO request = new RegisterUserRequestDTO("Carlos", "CARLOS@MAIL.COM", "12345678");
		when(userRepository.existsByEmailIgnoreCase("carlos@mail.com")).thenReturn(false);
		when(passwordEncoder.encode("12345678")).thenReturn("encoded-password");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User user = invocation.getArgument(0);
			user.setId(UUID.randomUUID());
			return user;
		});

		RegisterUserResponseDTO response = useCase.execute(request);

		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());
		User savedUser = userCaptor.getValue();

		assertThat(savedUser.getEmail()).isEqualTo("carlos@mail.com");
		assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
		assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
		assertThat(response.role()).isEqualTo("USER");
	}

	@Test
	void shouldRejectDuplicateEmail() {
		RegisterUserRequestDTO request = new RegisterUserRequestDTO("Carlos", "carlos@mail.com", "12345678");
		when(userRepository.existsByEmailIgnoreCase("carlos@mail.com")).thenReturn(true);

		assertThatThrownBy(() -> useCase.execute(request))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Email is already registered");
	}
}
