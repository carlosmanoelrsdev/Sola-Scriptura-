package br.com.carlos.solascriptura.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

	@Test
	void shouldLoginRegisteredUser() throws Exception {
		registerDefaultUser();

		String payload = """
				{
				  "email": "carlos@mail.com",
				  "password": "12345678"
				}
				""";

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists())
				.andExpect(jsonPath("$.tokenType", is("Bearer")))
				.andExpect(jsonPath("$.expiresIn", is(3600000)))
				.andExpect(jsonPath("$.user.email", is("carlos@mail.com")))
				.andExpect(jsonPath("$.user.role", is("USER")))
				.andExpect(jsonPath("$.user.password").doesNotExist());
	}

	@Test
	void shouldRejectInvalidLogin() throws Exception {
		registerDefaultUser();

		String payload = """
				{
				  "email": "carlos@mail.com",
				  "password": "wrong-password"
				}
				""";

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message", is("Invalid credentials")));
	}

	@Test
	void shouldRequireAuthenticationToDeleteAccount() throws Exception {
		mockMvc.perform(delete("/api/users/me"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldDeleteAuthenticatedAccount() throws Exception {
		registerDefaultUser();
		String token = loginDefaultUser();

		mockMvc.perform(delete("/api/users/me")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNoContent());

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "email": "carlos@mail.com",
						  "password": "12345678"
						}
						"""))
				.andExpect(status().isUnauthorized());
	}

	private void registerDefaultUser() throws Exception {
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "name": "Carlos",
						  "email": "carlos@mail.com",
						  "password": "12345678"
						}
						"""))
				.andExpect(status().isCreated());
	}

	private String loginDefaultUser() throws Exception {
		String response = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "email": "carlos@mail.com",
						  "password": "12345678"
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token", startsWith("ey")))
				.andReturn()
				.getResponse()
				.getContentAsString();

		return response.substring(response.indexOf("\"token\":\"") + 9, response.indexOf("\",\"tokenType\""));
	}
}
