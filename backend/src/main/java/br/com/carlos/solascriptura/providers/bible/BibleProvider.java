package br.com.carlos.solascriptura.providers.bible;

import java.util.List;

import br.com.carlos.solascriptura.dto.bible.BibleBookResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleChapterResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleSearchResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleVerseResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleVersionResponseDTO;

public interface BibleProvider {

	List<BibleVersionResponseDTO> getVersions();

	List<BibleBookResponseDTO> getBooks();

	List<BibleBookResponseDTO> getBooksByVersion(String version);

	BibleBookResponseDTO getBook(String version, String book);

	BibleChapterResponseDTO getChapter(String version, String book, int chapter);

	BibleVerseResponseDTO getVerse(String version, String book, int chapter, int verse);

	BibleVerseResponseDTO getRandomVerse(String version);

	BibleSearchResponseDTO search(String version, String query, int limit, int offset);
}
