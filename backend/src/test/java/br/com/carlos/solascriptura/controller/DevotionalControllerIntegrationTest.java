package br.com.carlos.solascriptura.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.carlos.solascriptura.repository.DevotionalRepository;
import br.com.carlos.solascriptura.repository.ReadingRepository;
import br.com.carlos.solascriptura.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class DevotionalControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ReadingRepository readingRepository;

	@Autowired
	private DevotionalRepository devotionalRepository;

	@BeforeEach
	void setUp() {
		devotionalRepository.deleteAll();
		readingRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void shouldRequireAuthenticationToCreateDevotional() throws Exception {
		mockMvc.perform(post("/api/devotionals")
				.contentType(MediaType.APPLICATION_JSON)
				.content(validPayload("Esperanca", "Conteudo devocional suficiente para teste.")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldCreateDevotionalWithReferences() throws Exception {
		String token = registerAndLogin("carlos@mail.com");

		mockMvc.perform(post("/api/devotionals")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(validPayload("Esperanca", "Conteudo devocional suficiente para teste.")))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.title", is("Esperanca")))
				.andExpect(jsonPath("$.content", is("Conteudo devocional suficiente para teste.")))
				.andExpect(jsonPath("$.references", hasSize(1)))
				.andExpect(jsonPath("$.references[0].book", is("jo")))
				.andExpect(jsonPath("$.references[0].version", is("ACF")));
	}

	@Test
	void shouldListOnlyAuthenticatedUserDevotionals() throws Exception {
		String firstToken = registerAndLogin("first@mail.com");
		String secondToken = registerAndLogin("second@mail.com");

		createDevotional(firstToken, "Primeiro");
		createDevotional(secondToken, "Segundo");

		mockMvc.perform(get("/api/devotionals")
				.header("Authorization", "Bearer " + firstToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].title", is("Primeiro")));
	}

	@Test
	void shouldGetUpdateAndDeleteOwnDevotional() throws Exception {
		String token = registerAndLogin("carlos@mail.com");
		String id = createDevotional(token, "Antes");

		mockMvc.perform(get("/api/devotionals/{id}", id)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title", is("Antes")));

		mockMvc.perform(put("/api/devotionals/{id}", id)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(validPayload("Depois", "Conteudo atualizado suficiente para teste.")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title", is("Depois")))
				.andExpect(jsonPath("$.references", hasSize(1)));

		mockMvc.perform(delete("/api/devotionals/{id}", id)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/devotionals/{id}", id)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
	}

	@Test
	void shouldPreventAccessToAnotherUsersDevotional() throws Exception {
		String firstToken = registerAndLogin("first@mail.com");
		String secondToken = registerAndLogin("second@mail.com");
		String id = createDevotional(firstToken, "Privado");

		mockMvc.perform(get("/api/devotionals/{id}", id)
				.header("Authorization", "Bearer " + secondToken))
				.andExpect(status().isNotFound());

		mockMvc.perform(put("/api/devotionals/{id}", id)
				.header("Authorization", "Bearer " + secondToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(validPayload("Invasao", "Conteudo atualizado suficiente para teste.")))
				.andExpect(status().isNotFound());

		mockMvc.perform(delete("/api/devotionals/{id}", id)
				.header("Authorization", "Bearer " + secondToken))
				.andExpect(status().isNotFound());
	}

	@Test
	void shouldRejectInvalidReferenceRange() throws Exception {
		String token = registerAndLogin("carlos@mail.com");

		mockMvc.perform(post("/api/devotionals")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "title": "Referencia invalida",
						  "content": "Conteudo devocional suficiente para teste.",
						  "references": [
						    {
						      "book": "jo",
						      "chapter": 3,
						      "startVerse": 16,
						      "endVerse": 15,
						      "version": "ACF"
						    }
						  ]
						}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("End verse must be greater than or equal to start verse")));
	}

	private String createDevotional(String token, String title) throws Exception {
		String response = mockMvc.perform(post("/api/devotionals")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(validPayload(title, "Conteudo devocional suficiente para teste.")))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();

		return response.substring(response.indexOf("\"id\":\"") + 6, response.indexOf("\",\"title\""));
	}

	private String validPayload(String title, String content) {
		return """
				{
				  "title": "%s",
				  "content": "%s",
				  "references": [
				    {
				      "book": "JO",
				      "chapter": 3,
				      "startVerse": 16,
				      "endVerse": 17,
				      "version": "acf"
				    }
				  ]
				}
				""".formatted(title, content);
	}

	private String registerAndLogin(String email) throws Exception {
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "name": "Carlos",
						  "email": "%s",
						  "password": "12345678"
						}
						""".formatted(email)))
				.andExpect(status().isCreated());

		String response = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "email": "%s",
						  "password": "12345678"
						}
						""".formatted(email)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token", startsWith("ey")))
				.andReturn()
				.getResponse()
				.getContentAsString();

		return response.substring(response.indexOf("\"token\":\"") + 9, response.indexOf("\",\"tokenType\""));
	}
}
