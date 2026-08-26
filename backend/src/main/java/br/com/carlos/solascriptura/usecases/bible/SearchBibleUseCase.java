package br.com.carlos.solascriptura.usecases.bible;

import org.springframework.stereotype.Component;

import br.com.carlos.solascriptura.dto.bible.BibleSearchResponseDTO;
import br.com.carlos.solascriptura.providers.bible.BibleProvider;

@Component
public class SearchBibleUseCase {

	private final BibleProvider bibleProvider;

	public SearchBibleUseCase(BibleProvider bibleProvider) {
		this.bibleProvider = bibleProvider;
	}

	public BibleSearchResponseDTO execute(String version, String query, int limit, int offset) {
		return bibleProvider.search(version.trim(), query.trim(), limit, offset);
	}
}
