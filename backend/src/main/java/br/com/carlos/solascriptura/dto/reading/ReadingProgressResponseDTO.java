package br.com.carlos.solascriptura.dto.reading;

public record ReadingProgressResponseDTO(
		long chaptersRead,
		int totalChapters,
		double percent,
		long booksCompleted,
		ReadingResponseDTO lastReading,
		int currentStreak,
		int longestStreak) {
}
