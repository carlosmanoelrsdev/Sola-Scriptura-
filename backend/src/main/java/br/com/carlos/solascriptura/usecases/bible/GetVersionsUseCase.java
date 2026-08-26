package br.com.carlos.solascriptura.usecases.bible;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.carlos.solascriptura.dto.bible.BibleVersionResponseDTO;
import br.com.carlos.solascriptura.providers.bible.BibleProvider;

@Component
public class GetVersionsUseCase {

	private final BibleProvider bibleProvider;

	public GetVersionsUseCase(BibleProvider bibleProvider) {
		this.bibleProvider = bibleProvider;
	}

	public List<BibleVersionResponseDTO> execute() {
		return bibleProvider.getVersions();
	}
}
