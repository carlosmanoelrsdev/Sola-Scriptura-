package br.com.carlos.solascriptura.usecases.bible;

import org.springframework.stereotype.Component;

import br.com.carlos.solascriptura.dto.bible.BibleBookResponseDTO;
import br.com.carlos.solascriptura.providers.bible.BibleProvider;

@Component
public class GetBookUseCase {

	private final BibleProvider bibleProvider;

	public GetBookUseCase(BibleProvider bibleProvider) {
		this.bibleProvider = bibleProvider;
	}

	public BibleBookResponseDTO execute(String version, String book) {
		return bibleProvider.getBook(version.trim(), book.trim());
	}
}
