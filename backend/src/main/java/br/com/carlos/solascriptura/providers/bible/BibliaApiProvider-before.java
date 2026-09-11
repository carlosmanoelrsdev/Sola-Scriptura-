package br.com.carlos.solascriptura.providers.bible;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;

import br.com.carlos.solascriptura.dto.bible.BibleBookResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleChapterInfoDTO;
import br.com.carlos.solascriptura.dto.bible.BibleChapterResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleSearchResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleSearchResultDTO;
import br.com.carlos.solascriptura.dto.bible.BibleVerseResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleVersionResponseDTO;
import br.com.carlos.solascriptura.exceptions.ExternalProviderException;
import br.com.carlos.solascriptura.exceptions.ResourceNotFoundException;

class BibliaApiProviderBefore implements BibleProvider {

	private final RestClient restClient;
	private final String apiKey;

	public BibliaApiProviderBefore(
			RestClient.Builder restClientBuilder,
			@Value("${app.bible.api.url}") String apiUrl,
			@Value("${app.bible.api.key}") String apiKey) {
		this.restClient = restClientBuilder.baseUrl(apiUrl + "/api/v2").build();
		this.apiKey = apiKey;
	}

	@Override
	public List<BibleVersionResponseDTO> getVersions() {
		JsonNode data = getData("/versions");
		List<BibleVersionResponseDTO> versions = new ArrayList<>();

		data.forEach(item -> versions.add(new BibleVersionResponseDTO(
				text(item, "code"),
				text(item, "copyright"),
				text(item, "permissions"),
				text(item, "language"))));

		return versions;
	}

	@Override
	public List<BibleBookResponseDTO> getBooks() {
		return readBooks("/books");
	}

	@Override
	public List<BibleBookResponseDTO> getBooksByVersion(String version) {
		return readBooks("/versions/{version}/books", version);
	}

	@Override
	public BibleBookResponseDTO getBook(String version, String book) {
		return toBook(getData("/versions/{version}/books/{book}", version, book));
	}

	@Override
	public BibleChapterResponseDTO getChapter(String version, String book, int chapter) {
		JsonNode data = getData("/versions/{version}/books/{book}/chapters/{chapter}", version, book, chapter);
		JsonNode bookNode = data.path("book");
		JsonNode chapterNode = data.path("chapter");
		List<BibleVerseResponseDTO> verses = new ArrayList<>();

		data.path("verses").forEach(verseNode -> verses.add(new BibleVerseResponseDTO(
				text(data, "reference") + ":" + number(verseNode, "number"),
				text(data, "version"),
				toBook(bookNode),
				number(chapterNode, "number"),
				number(verseNode, "number"),
				text(verseNode, "text"))));

		return new BibleChapterResponseDTO(
				text(data, "reference"),
				text(data, "version"),
				toBook(bookNode),
				new BibleChapterInfoDTO(number(chapterNode, "number"), number(chapterNode, "verses")),
				verses);
	}

	@Override
	public BibleVerseResponseDTO getVerse(String version, String book, int chapter, int verse) {
		return toVerse(getData("/versions/{version}/books/{book}/chapters/{chapter}/verses/{verse}",
				version, book, chapter, verse));
	}

	@Override
	public BibleVerseResponseDTO getRandomVerse(String version) {
		return toVerse(getData("/versions/{version}/random", version));
	}

	@Override
	public BibleSearchResponseDTO search(String version, String query, int limit, int offset) {
		JsonNode data = getDataWithQuery("/versions/{version}/search", version, query, limit, offset);
		List<BibleSearchResultDTO> results = new ArrayList<>();

		data.path("results").forEach(item -> results.add(new BibleSearchResultDTO(
				text(item, "reference"),
				toBook(item.path("book")),
				number(item, "chapter"),
				number(item, "verse"),
				text(item, "text"))));

		return new BibleSearchResponseDTO(
				text(data, "query"),
				text(data, "version"),
				number(data, "limit"),
				number(data, "offset"),
				results);
	}

	private List<BibleBookResponseDTO> readBooks(String path, Object... uriVariables) {
		JsonNode data = getData(path, uriVariables);
		List<BibleBookResponseDTO> books = new ArrayList<>();

		data.forEach(item -> books.add(toBook(item)));

		return books;
	}

	private JsonNode getData(String path, Object... uriVariables) {
		try {
			JsonNode response = restClient.get()
					.uri(path, uriVariables)
					.headers(this::addAuthorization)
					.retrieve()
					.body(JsonNode.class);

			return response == null ? null : response.path("data");
		} catch (HttpStatusCodeException ex) {
			throw mapProviderException(ex);
		} catch (Exception ex) {
			throw new ExternalProviderException("Bible provider is unavailable");
		}
	}

	private JsonNode getDataWithQuery(String path, String version, String query, int limit, int offset) {
		try {
			JsonNode response = restClient.get()
					.uri(uriBuilder -> uriBuilder
							.path(path)
							.queryParam("q", query)
							.queryParam("limit", limit)
							.queryParam("offset", offset)
							.build(version))
					.headers(this::addAuthorization)
					.retrieve()
					.body(JsonNode.class);

			return response == null ? null : response.path("data");
		} catch (HttpStatusCodeException ex) {
			throw mapProviderException(ex);
		} catch (Exception ex) {
			throw new ExternalProviderException("Bible provider is unavailable");
		}
	}

	private RuntimeException mapProviderException(HttpStatusCodeException ex) {
		if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
			return new ResourceNotFoundException("Bible resource not found");
		}

		if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			return new ExternalProviderException("Bible provider rejected the configured API key");
		}

		if (ex.getStatusCode().value() == 429) {
			return new ExternalProviderException("Bible provider quota exceeded");
		}

		return new ExternalProviderException("Bible provider request failed");
	}

	private void addAuthorization(HttpHeaders headers) {
		if (apiKey != null && !apiKey.isBlank()) {
			headers.setBearerAuth(apiKey);
		}
	}

	private BibleBookResponseDTO toBook(JsonNode node) {
		return new BibleBookResponseDTO(
				number(node, "id"),
				text(node, "name"),
				text(node, "abbrev"),
				text(node, "testament"));
	}

	private BibleVerseResponseDTO toVerse(JsonNode data) {
		return new BibleVerseResponseDTO(
				text(data, "reference"),
				text(data, "version"),
				toBook(data.path("book")),
				number(data, "chapter"),
				number(data, "verse"),
				text(data, "text"));
	}

	private String text(JsonNode node, String field) {
		JsonNode value = node.path(field);
		return value.isMissingNode() || value.isNull() ? null : value.asText();
	}

	private int number(JsonNode node, String field) {
		return node.path(field).asInt();
	}
}
