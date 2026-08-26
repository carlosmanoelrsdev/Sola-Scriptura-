package br.com.carlos.solascriptura.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.carlos.solascriptura.entities.Devotional;
import br.com.carlos.solascriptura.entities.User;

public interface DevotionalRepository extends JpaRepository<Devotional, UUID> {

	List<Devotional> findByUserOrderByUpdatedAtDesc(User user);

	Optional<Devotional> findByIdAndUser(UUID id, User user);

	void deleteByUser(User user);
}
