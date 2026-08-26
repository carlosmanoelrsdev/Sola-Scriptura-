package br.com.carlos.solascriptura.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.carlos.solascriptura.entities.Reading;
import br.com.carlos.solascriptura.entities.User;

public interface ReadingRepository extends JpaRepository<Reading, UUID> {

	boolean existsByUserAndVersionIgnoreCaseAndBookIgnoreCaseAndChapter(
			User user,
			String version,
			String book,
			int chapter);

	List<Reading> findByUserOrderByReadAtDesc(User user);

	Optional<Reading> findFirstByUserOrderByReadAtDesc(User user);

	void deleteByUser(User user);
}
