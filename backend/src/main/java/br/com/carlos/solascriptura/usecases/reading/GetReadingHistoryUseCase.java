package br.com.carlos.solascriptura.usecases.reading;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.carlos.solascriptura.dto.reading.ReadingResponseDTO;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.repository.ReadingRepository;

@Component
public class GetReadingHistoryUseCase {

	private final ReadingRepository readingRepository;
	private final GetAuthenticatedUser getAuthenticatedUser;

	public GetReadingHistoryUseCase(ReadingRepository readingRepository, GetAuthenticatedUser getAuthenticatedUser) {
		this.readingRepository = readingRepository;
		this.getAuthenticatedUser = getAuthenticatedUser;
	}

	@Transactional(readOnly = true)
	public List<ReadingResponseDTO> execute(String authenticatedEmail) {
		User user = getAuthenticatedUser.execute(authenticatedEmail);
		return readingRepository.findByUserOrderByReadAtDesc(user)
				.stream()
				.map(ReadingMapper::toResponse)
				.toList();
	}
}
