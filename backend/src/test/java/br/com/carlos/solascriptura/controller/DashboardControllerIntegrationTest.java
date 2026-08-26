package br.com.carlos.solascriptura.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.carlos.solascriptura.dto.bible.BibleBookResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleVerseResponseDTO;
import br.com.carlos.solascriptura.providers.bible.BibleProvider;
import br.com.carlos.solascriptura.repository.DevotionalRepository;
import br.com.carlos.solascriptura.repository.ReadingRepository;
import br.com.carlos.solascriptura.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ReadingRepository readingRepository;

	@Autowired
	private DevotionalRepository devotionalRepository;

	@MockBean
	private BibleProvider bibleProvider;

	@BeforeEach
	void setUp() {
		devotionalRepository.deleteAll();
		readingRepository.deleteAll();
		userRepository.deleteAll();

		when(bibleProvider.getRandomVerse(anyString())).thenReturn(new BibleVerseResponseDTO(
				"Joao 3:16",
				"ACF",
				new BibleBookResponseDTO(43, "Joao", "jo", "NT"),
				3,
				16,
				"Porque Deus amou o mundo."));
	}

	@Test
	void shouldRequireAuthentication() throws Exception {
		mockMvc.perform(get("/api/dashboard"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message", is("Authentication required")))
				.andExpect(jsonPath("$.path", is("/api/dashboard")));
	}

	@Test
	void shouldReturnDashboardForAuthenticatedUser() throws Exception {
		String token = registerAndLogin("carlos@mail.com");

		registerReading(token);
		createDevotional(token, "Primeiro");
		createDevotional(token, "Segundo");

		mockMvc.perform(get("/api/dashboard")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.progress.chaptersRead", is(1)))
				.andExpect(jsonPath("$.progress.currentStreak", is(1)))
				.andExpect(jsonPath("$.progress.lastReading.book", is("jo")))
				.andExpect(jsonPath("$.verseOfDay.reference", is("Joao 3:16")))
				.andExpect(jsonPath("$.recentDevotionals", hasSize(2)));
	}

	private void registerReading(String token) throws Exception {
		mockMvc.perform(post("/api/readings")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "version": "ACF",
						  "book": "jo",
						  "chapter": 3
						}
						"""))
				.andExpect(status().isCreated());
	}

	private void createDevotional(String token, String title) throws Exception {
		mockMvc.perform(post("/api/devotionals")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "title": "%s",
						  "content": "Conteudo devocional suficiente para teste.",
						  "references": []
						}
						""".formatted(title)))
				.andExpect(status().isCreated());
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
