package br.com.carlos.solascriptura.usecases.reading;

import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.carlos.solascriptura.dto.reading.ReadingProgressResponseDTO;
import br.com.carlos.solascriptura.dto.reading.ReadingResponseDTO;
import br.com.carlos.solascriptura.entities.Reading;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.repository.ReadingRepository;

@Component
public class GetReadingProgressUseCase {

	private final ReadingRepository readingRepository;
	private final GetAuthenticatedUser getAuthenticatedUser;
	private final ZoneId zoneId;

	public GetReadingProgressUseCase(
			ReadingRepository readingRepository,
			GetAuthenticatedUser getAuthenticatedUser,
			@Value("${app.timezone:America/Sao_Paulo}") String timezone) {
		this.readingRepository = readingRepository;
		this.getAuthenticatedUser = getAuthenticatedUser;
		this.zoneId = ZoneId.of(timezone);
	}

	@Transactional(readOnly = true)
	public ReadingProgressResponseDTO execute(String authenticatedEmail) {
		User user = getAuthenticatedUser.execute(authenticatedEmail);
		List<Reading> readings = readingRepository.findByUserOrderByReadAtDesc(user);
		long chaptersRead = readings.size();
		double percent = Math.round((chaptersRead * 10000.0 / BibleBookChapterCatalog.TOTAL_CHAPTERS)) / 100.0;
		ReadingResponseDTO lastReading = readings.stream()
				.findFirst()
				.map(ReadingMapper::toResponse)
				.orElse(null);

		return new ReadingProgressResponseDTO(
				chaptersRead,
				BibleBookChapterCatalog.TOTAL_CHAPTERS,
				percent,
				ReadingStatsCalculator.booksCompleted(readings),
				lastReading,
				ReadingStatsCalculator.currentStreak(readings, zoneId),
				ReadingStatsCalculator.longestStreak(readings, zoneId));
	}
}
