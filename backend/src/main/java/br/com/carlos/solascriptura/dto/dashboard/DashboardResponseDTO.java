package br.com.carlos.solascriptura.dto.dashboard;

import java.util.List;

import br.com.carlos.solascriptura.dto.bible.BibleVerseResponseDTO;
import br.com.carlos.solascriptura.dto.devotional.DevotionalResponseDTO;
import br.com.carlos.solascriptura.dto.reading.ReadingProgressResponseDTO;

public record DashboardResponseDTO(
		ReadingProgressResponseDTO progress,
		BibleVerseResponseDTO verseOfDay,
		List<DevotionalResponseDTO> recentDevotionals) {
}
