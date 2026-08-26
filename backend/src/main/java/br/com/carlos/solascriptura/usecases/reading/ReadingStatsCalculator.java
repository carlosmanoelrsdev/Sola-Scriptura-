package br.com.carlos.solascriptura.usecases.reading;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.carlos.solascriptura.entities.Reading;

final class ReadingStatsCalculator {

	private ReadingStatsCalculator() {
	}

	static int currentStreak(List<Reading> readings, ZoneId zoneId) {
		Set<LocalDate> readingDates = readingDates(readings, zoneId);
		LocalDate cursor = LocalDate.now(zoneId);

		if (!readingDates.contains(cursor)) {
			cursor = cursor.minusDays(1);
		}

		int streak = 0;
		while (readingDates.contains(cursor)) {
			streak++;
			cursor = cursor.minusDays(1);
		}

		return streak;
	}

	static int longestStreak(List<Reading> readings, ZoneId zoneId) {
		List<LocalDate> dates = readingDates(readings, zoneId)
				.stream()
				.sorted()
				.toList();

		int longest = 0;
		int current = 0;
		LocalDate previous = null;

		for (LocalDate date : dates) {
			if (previous == null || date.equals(previous.plusDays(1))) {
				current++;
			} else {
				current = 1;
			}

			longest = Math.max(longest, current);
			previous = date;
		}

		return longest;
	}

	static long booksCompleted(List<Reading> readings) {
		Map<String, Set<Integer>> chaptersByBook = new HashMap<>();

		for (Reading reading : readings) {
			chaptersByBook
					.computeIfAbsent(reading.getBook().toLowerCase(), key -> new HashSet<>())
					.add(reading.getChapter());
		}

		return chaptersByBook.entrySet()
				.stream()
				.filter(entry -> entry.getValue().size() >= BibleBookChapterCatalog.chapterCount(entry.getKey()))
				.count();
	}

	private static Set<LocalDate> readingDates(List<Reading> readings, ZoneId zoneId) {
		return readings.stream()
				.map(reading -> reading.getReadAt().atZoneSameInstant(zoneId).toLocalDate())
				.collect(Collectors.toSet());
	}
}
