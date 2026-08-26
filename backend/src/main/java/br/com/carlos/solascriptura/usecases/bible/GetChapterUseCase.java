package br.com.carlos.solascriptura.usecases.bible;

import org.springframework.stereotype.Component;

import br.com.carlos.solascriptura.dto.bible.BibleChapterResponseDTO;
import br.com.carlos.solascriptura.providers.bible.BibleProvider;

@Component
public class GetChapterUseCase {

	private final BibleProvider bibleProvider;

	public GetChapterUseCase(BibleProvider bibleProvider) {
		this.bibleProvider = bibleProvider;
	}

	public BibleChapterResponseDTO execute(String version, String book, int chapter) {
		return bibleProvider.getChapter(version.trim(), book.trim(), chapter);
	}
}
