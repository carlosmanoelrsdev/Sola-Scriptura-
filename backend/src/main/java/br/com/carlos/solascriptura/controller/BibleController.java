package br.com.carlos.solascriptura.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.carlos.solascriptura.dto.bible.BibleBookResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleChapterResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleSearchResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleVerseResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleVersionResponseDTO;
import br.com.carlos.solascriptura.usecases.bible.GetBookUseCase;
import br.com.carlos.solascriptura.usecases.bible.GetBooksUseCase;
import br.com.carlos.solascriptura.usecases.bible.GetChapterUseCase;
import br.com.carlos.solascriptura.usecases.bible.GetRandomVerseUseCase;
import br.com.carlos.solascriptura.usecases.bible.GetVerseUseCase;
import br.com.carlos.solascriptura.usecases.bible.GetVersionsUseCase;
import br.com.carlos.solascriptura.usecases.bible.SearchBibleUseCase;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@Validated
@RequestMapping("/api/bible")
public class BibleController {

	private final GetVersionsUseCase getVersionsUseCase;
	private final GetBooksUseCase getBooksUseCase;
	private final GetBookUseCase getBookUseCase;
	private final GetChapterUseCase getChapterUseCase;
	private final GetVerseUseCase getVerseUseCase;
	private final GetRandomVerseUseCase getRandomVerseUseCase;
	private final SearchBibleUseCase searchBibleUseCase;

	public BibleController(
			GetVersionsUseCase getVersionsUseCase,
			GetBooksUseCase getBooksUseCase,
			GetBookUseCase getBookUseCase,
			GetChapterUseCase getChapterUseCase,
			GetVerseUseCase getVerseUseCase,
			GetRandomVerseUseCase getRandomVerseUseCase,
			SearchBibleUseCase searchBibleUseCase) {
		this.getVersionsUseCase = getVersionsUseCase;
		this.getBooksUseCase = getBooksUseCase;
		this.getBookUseCase = getBookUseCase;
		this.getChapterUseCase = getChapterUseCase;
		this.getVerseUseCase = getVerseUseCase;
		this.getRandomVerseUseCase = getRandomVerseUseCase;
		this.searchBibleUseCase = searchBibleUseCase;
	}

	@GetMapping("/versions")
	public ResponseEntity<List<BibleVersionResponseDTO>> getVersions() {
		return ResponseEntity.ok(getVersionsUseCase.execute());
	}

	@GetMapping("/books")
	public ResponseEntity<List<BibleBookResponseDTO>> getBooks(
			@RequestParam(required = false) String version) {
		return ResponseEntity.ok(getBooksUseCase.execute(version));
	}

	@GetMapping("/versions/{version}/books/{book}")
	public ResponseEntity<BibleBookResponseDTO> getBook(
			@PathVariable @NotBlank String version,
			@PathVariable @NotBlank String book) {
		return ResponseEntity.ok(getBookUseCase.execute(version, book));
	}

	@GetMapping("/versions/{version}/books/{book}/chapters/{chapter}")
	public ResponseEntity<BibleChapterResponseDTO> getChapter(
			@PathVariable @NotBlank String version,
			@PathVariable @NotBlank String book,
			@PathVariable @Min(1) int chapter) {
		return ResponseEntity.ok(getChapterUseCase.execute(version, book, chapter));
	}

	@GetMapping("/versions/{version}/books/{book}/chapters/{chapter}/verses/{verse}")
	public ResponseEntity<BibleVerseResponseDTO> getVerse(
			@PathVariable @NotBlank String version,
			@PathVariable @NotBlank String book,
			@PathVariable @Min(1) int chapter,
			@PathVariable @Min(1) int verse) {
		return ResponseEntity.ok(getVerseUseCase.execute(version, book, chapter, verse));
	}

	@GetMapping("/versions/{version}/random")
	public ResponseEntity<BibleVerseResponseDTO> getRandomVerse(
			@PathVariable @NotBlank String version) {
		return ResponseEntity.ok(getRandomVerseUseCase.execute(version));
	}

	@GetMapping("/versions/{version}/search")
	public ResponseEntity<BibleSearchResponseDTO> search(
			@PathVariable @NotBlank String version,
			@RequestParam("q") @NotBlank @Size(min = 2, max = 120) String query,
			@RequestParam(defaultValue = "20") @Min(1) @Max(50) int limit,
			@RequestParam(defaultValue = "0") @Min(0) int offset) {
		return ResponseEntity.ok(searchBibleUseCase.execute(version, query, limit, offset));
	}
}
