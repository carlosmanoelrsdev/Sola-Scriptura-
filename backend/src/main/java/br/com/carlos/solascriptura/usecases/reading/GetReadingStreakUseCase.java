package br.com.carlos.solascriptura.usecases.reading;

import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.carlos.solascriptura.dto.reading.ReadingStreakResponseDTO;
import br.com.carlos.solascriptura.entities.Reading;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.repository.ReadingRepository;

@Component
public class GetReadingStreakUseCase {

	private final ReadingRepository readingRepository;
	private final GetAuthenticatedUser getAuthenticatedUser;
	private final ZoneId zoneId;

	public GetReadingStreakUseCase(
			ReadingRepository readingRepository,
			GetAuthenticatedUser getAuthenticatedUser,
			@Value("${app.timezone:America/Sao_Paulo}") String timezone) {
		this.readingRepository = readingRepository;
		this.getAuthenticatedUser = getAuthenticatedUser;
		this.zoneId = ZoneId.of(timezone);
	}

	@Transactional(readOnly = true)
	public ReadingStreakResponseDTO execute(String authenticatedEmail) {
		User user = getAuthenticatedUser.execute(authenticatedEmail);
		List<Reading> readings = readingRepository.findByUserOrderByReadAtDesc(user);

		return new ReadingStreakResponseDTO(
				ReadingStatsCalculator.currentStreak(readings, zoneId),
				ReadingStatsCalculator.longestStreak(readings, zoneId));
	}
}
