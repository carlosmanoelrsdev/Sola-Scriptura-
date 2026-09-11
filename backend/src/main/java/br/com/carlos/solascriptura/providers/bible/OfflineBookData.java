package br.com.carlos.solascriptura.providers.bible;

import java.util.List;

public record OfflineBookData(
        int id,
        String name,
        String abbrev,
        String testament,
        List<OfflineChapterData> chapters) {
}
