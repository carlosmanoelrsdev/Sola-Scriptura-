package br.com.carlos.solascriptura.usecases.bible;

import org.springframework.stereotype.Component;

import br.com.carlos.solascriptura.dto.bible.BibleVerseResponseDTO;
import br.com.carlos.solascriptura.providers.bible.BibleProvider;

@Component
public class GetVerseUseCase {

	private final BibleProvider bibleProvider;

	public GetVerseUseCase(BibleProvider bibleProvider) {
		this.bibleProvider = bibleProvider;
	}

	public BibleVerseResponseDTO execute(String version, String book, int chapter, int verse) {
		return bibleProvider.getVerse(version.trim(), book.trim(), chapter, verse);
	}
}
