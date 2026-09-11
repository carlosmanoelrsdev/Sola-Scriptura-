package br.com.carlos.solascriptura.providers.bible;

public record OfflineVerseData(
        String reference,
        String version,
        OfflineBookData book,
        int chapter,
        int verse,
        String text) {
}
