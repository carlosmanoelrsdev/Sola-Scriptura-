package br.com.carlos.solascriptura.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.carlos.solascriptura.entities.DevotionalReference;

public interface DevotionalReferenceRepository extends JpaRepository<DevotionalReference, UUID> {
}
