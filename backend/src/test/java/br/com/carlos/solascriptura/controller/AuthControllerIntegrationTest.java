package br.com.carlos.solascriptura.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.carlos.solascriptura.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
	}

	@Test
	void shouldRegisterUser() throws Exception {
		String payload = """
				{
				  "name": "Carlos",
				  "email": "carlos@mail.com",
				  "password": "12345678"
				}
				""";

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name", is("Carlos")))
				.andExpect(jsonPath("$.email", is("carlos@mail.com")))
				.andExpect(jsonPath("$.role", is("USER")))
				.andExpect(jsonPath("$.password").doesNotExist());
	}

	@Test
	void shouldRejectDuplicateEmail() throws Exception {
		String payload = """
				{
				  "name": "Carlos",
				  "email": "carlos@mail.com",
				  "password": "12345678"
				}
				""";

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Email is already registered")));
	}

	@Test
	void shouldValidateRequestBody() throws Exception {
		String payload = """
				{
				  "name": "",
				  "email": "invalid",
				  "password": "123"
				}
				""";

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Validation failed")))
				.andExpect(jsonPath("$.validationErrors.name").exists())
				.andExpect(jsonPath("$.validationErrors.email").exists())
				.andExpect(jsonPath("$.validationErrors.password").exists());
	}
}
