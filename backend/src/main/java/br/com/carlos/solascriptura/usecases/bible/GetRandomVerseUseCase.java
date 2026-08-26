package br.com.carlos.solascriptura.usecases.bible;

import org.springframework.stereotype.Component;

import br.com.carlos.solascriptura.dto.bible.BibleVerseResponseDTO;
import br.com.carlos.solascriptura.providers.bible.BibleProvider;

@Component
public class GetRandomVerseUseCase {

	private final BibleProvider bibleProvider;

	public GetRandomVerseUseCase(BibleProvider bibleProvider) {
		this.bibleProvider = bibleProvider;
	}

	public BibleVerseResponseDTO execute(String version) {
		return bibleProvider.getRandomVerse(version.trim());
	}
}
