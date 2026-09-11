package br.com.carlos.solascriptura.usecases.dashboard;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.carlos.solascriptura.dto.bible.BibleVerseResponseDTO;
import br.com.carlos.solascriptura.dto.dashboard.DashboardResponseDTO;
import br.com.carlos.solascriptura.dto.devotional.DevotionalResponseDTO;
import br.com.carlos.solascriptura.dto.reading.ReadingProgressResponseDTO;
import br.com.carlos.solascriptura.exceptions.ExternalProviderException;
import br.com.carlos.solascriptura.usecases.bible.GetRandomVerseUseCase;
import br.com.carlos.solascriptura.usecases.devotional.ListDevotionalsUseCase;
import br.com.carlos.solascriptura.usecases.reading.GetReadingProgressUseCase;

@Component
public class GetDashboardUseCase {

	private static final String DEFAULT_VERSE_VERSION = "ara";
	private static final int RECENT_DEVOTIONALS_LIMIT = 5;

	private final GetReadingProgressUseCase getReadingProgressUseCase;
	private final ListDevotionalsUseCase listDevotionalsUseCase;
	private final GetRandomVerseUseCase getRandomVerseUseCase;

	public GetDashboardUseCase(
			GetReadingProgressUseCase getReadingProgressUseCase,
			ListDevotionalsUseCase listDevotionalsUseCase,
			GetRandomVerseUseCase getRandomVerseUseCase) {
		this.getReadingProgressUseCase = getReadingProgressUseCase;
		this.listDevotionalsUseCase = listDevotionalsUseCase;
		this.getRandomVerseUseCase = getRandomVerseUseCase;
	}

	public DashboardResponseDTO execute(String authenticatedEmail) {
		ReadingProgressResponseDTO progress = getReadingProgressUseCase.execute(authenticatedEmail);
		List<DevotionalResponseDTO> recentDevotionals = listDevotionalsUseCase.execute(authenticatedEmail)
				.stream()
				.limit(RECENT_DEVOTIONALS_LIMIT)
				.toList();

		return new DashboardResponseDTO(progress, getVerseOfDay(), recentDevotionals);
	}

	private BibleVerseResponseDTO getVerseOfDay() {
		try {
			return getRandomVerseUseCase.execute(DEFAULT_VERSE_VERSION);
		} catch (Exception ex) {
			return null;
		}
	}
}
