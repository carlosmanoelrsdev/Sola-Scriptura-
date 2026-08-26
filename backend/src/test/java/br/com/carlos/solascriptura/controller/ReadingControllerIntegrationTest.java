package br.com.carlos.solascriptura.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.carlos.solascriptura.entities.Reading;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.repository.ReadingRepository;
import br.com.carlos.solascriptura.repository.UserRepository;
import br.com.carlos.solascriptura.repository.DevotionalRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ReadingControllerIntegrationTest {

	private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

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
	void shouldRequireAuthenticationToRegisterReading() throws Exception {
		mockMvc.perform(post("/api/readings")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "version": "ACF",
						  "book": "jo",
						  "chapter": 3
						}
						"""))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void shouldRegisterReadingForAuthenticatedUser() throws Exception {
		String token = registerAndLogin("carlos@mail.com");

		mockMvc.perform(post("/api/readings")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "version": "acf",
						  "book": "JO",
						  "chapter": 3
						}
						"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.version", is("ACF")))
				.andExpect(jsonPath("$.book", is("jo")))
				.andExpect(jsonPath("$.chapter", is(3)));
	}

	@Test
	void shouldRejectDuplicateReading() throws Exception {
		String token = registerAndLogin("carlos@mail.com");
		String payload = """
				{
				  "version": "ACF",
				  "book": "jo",
				  "chapter": 3
				}
				""";

		mockMvc.perform(post("/api/readings")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/readings")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", is("Chapter is already marked as read")));
	}

	@Test
	void shouldListOnlyAuthenticatedUserHistory() throws Exception {
		String firstToken = registerAndLogin("first@mail.com");
		String secondToken = registerAndLogin("second@mail.com");

		registerReading(firstToken, "jo", 3);
		registerReading(secondToken, "rm", 8);

		mockMvc.perform(get("/api/readings/history")
				.header("Authorization", "Bearer " + firstToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].book", is("jo")))
				.andExpect(jsonPath("$[0].chapter", is(3)));
	}

	@Test
	void shouldCalculateStreakFromDistinctReadingDays() throws Exception {
		String token = registerAndLogin("carlos@mail.com");
		User user = userRepository.findByEmailIgnoreCase("carlos@mail.com").orElseThrow();

		saveReading(user, "jo", 1, daysAgo(2));
		saveReading(user, "jo", 2, daysAgo(1));
		saveReading(user, "jo", 3, daysAgo(0));
		saveReading(user, "rm", 8, daysAgo(0));

		mockMvc.perform(get("/api/readings/streak")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.currentStreak", is(3)))
				.andExpect(jsonPath("$.longestStreak", is(3)));
	}

	@Test
	void shouldReturnProgressAndLastReading() throws Exception {
		String token = registerAndLogin("carlos@mail.com");
		User user = userRepository.findByEmailIgnoreCase("carlos@mail.com").orElseThrow();

		saveReading(user, "ob", 1, daysAgo(1));
		saveReading(user, "jo", 3, daysAgo(0));

		mockMvc.perform(get("/api/readings/progress")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.chaptersRead", is(2)))
				.andExpect(jsonPath("$.totalChapters", is(1189)))
				.andExpect(jsonPath("$.percent", is(0.17)))
				.andExpect(jsonPath("$.booksCompleted", is(1)))
				.andExpect(jsonPath("$.lastReading.book", is("jo")))
				.andExpect(jsonPath("$.currentStreak", is(2)))
				.andExpect(jsonPath("$.longestStreak", is(2)));
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

	private void registerReading(String token, String book, int chapter) throws Exception {
		mockMvc.perform(post("/api/readings")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
						  "version": "ACF",
						  "book": "%s",
						  "chapter": %d
						}
						""".formatted(book, chapter)))
				.andExpect(status().isCreated());
	}

	private void saveReading(User user, String book, int chapter, OffsetDateTime readAt) {
		Reading reading = new Reading();
		reading.setUser(user);
		reading.setVersion("ACF");
		reading.setBook(book);
		reading.setChapter(chapter);
		reading.setReadAt(readAt);
		readingRepository.save(reading);
	}

	private OffsetDateTime daysAgo(int days) {
		return OffsetDateTime.now(ZONE_ID).minusDays(days);
	}
}
