package br.com.carlos.solascriptura.providers.bible;

import java.util.List;

public record OfflineBibleData(
        String version,
        List<OfflineBookData> books) {
}
