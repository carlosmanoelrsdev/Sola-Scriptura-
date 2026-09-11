package br.com.carlos.solascriptura.providers.bible;

import java.util.List;

public record OfflineChapterData(
        int number,
        String reference,
        List<OfflineVerseData> verses) {
}
