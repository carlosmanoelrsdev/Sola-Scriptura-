package br.com.carlos.solascriptura.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.carlos.solascriptura.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {

	boolean existsByEmailIgnoreCase(String email);

	Optional<User> findByEmailIgnoreCase(String email);
}
