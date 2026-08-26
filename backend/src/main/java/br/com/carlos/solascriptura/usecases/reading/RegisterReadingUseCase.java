package br.com.carlos.solascriptura.usecases.reading;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.carlos.solascriptura.dto.reading.ReadingResponseDTO;
import br.com.carlos.solascriptura.dto.reading.RegisterReadingRequestDTO;
import br.com.carlos.solascriptura.entities.Reading;
import br.com.carlos.solascriptura.entities.User;
import br.com.carlos.solascriptura.exceptions.BusinessException;
import br.com.carlos.solascriptura.repository.ReadingRepository;

@Component
public class RegisterReadingUseCase {

	private final ReadingRepository readingRepository;
	private final GetAuthenticatedUser getAuthenticatedUser;

	public RegisterReadingUseCase(ReadingRepository readingRepository, GetAuthenticatedUser getAuthenticatedUser) {
		this.readingRepository = readingRepository;
		this.getAuthenticatedUser = getAuthenticatedUser;
	}

	@Transactional
	public ReadingResponseDTO execute(String authenticatedEmail, RegisterReadingRequestDTO request) {
		User user = getAuthenticatedUser.execute(authenticatedEmail);
		String version = request.version().trim().toUpperCase();
		String book = request.book().trim().toLowerCase();

		if (readingRepository.existsByUserAndVersionIgnoreCaseAndBookIgnoreCaseAndChapter(
				user, version, book, request.chapter())) {
			throw new BusinessException("Chapter is already marked as read");
		}

		Reading reading = new Reading();
		reading.setUser(user);
		reading.setVersion(version);
		reading.setBook(book);
		reading.setChapter(request.chapter());

		return ReadingMapper.toResponse(readingRepository.save(reading));
	}
}
