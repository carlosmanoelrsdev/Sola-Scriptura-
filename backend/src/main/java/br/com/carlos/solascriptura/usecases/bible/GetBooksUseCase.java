package br.com.carlos.solascriptura.usecases.bible;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.carlos.solascriptura.dto.bible.BibleBookResponseDTO;
import br.com.carlos.solascriptura.providers.bible.BibleProvider;

@Component
public class GetBooksUseCase {

	private final BibleProvider bibleProvider;

	public GetBooksUseCase(BibleProvider bibleProvider) {
		this.bibleProvider = bibleProvider;
	}

	public List<BibleBookResponseDTO> execute(String version) {
		if (version == null || version.isBlank()) {
			return bibleProvider.getBooks();
		}

		return bibleProvider.getBooksByVersion(version.trim());
	}
}
