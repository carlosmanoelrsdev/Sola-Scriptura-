package br.com.carlos.solascriptura.providers.bible;

import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.carlos.solascriptura.dto.bible.BibleBookResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleChapterInfoDTO;
import br.com.carlos.solascriptura.dto.bible.BibleChapterResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleSearchResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleSearchResultDTO;
import br.com.carlos.solascriptura.dto.bible.BibleVerseResponseDTO;
import br.com.carlos.solascriptura.dto.bible.BibleVersionResponseDTO;

public class OfflineBibleLoader {

    private final ObjectMapper mapper = new ObjectMapper();

    public OfflineBibleData read(String version) {
        String normalizedVersion = normalizeVersion(version);

        try {
            String path = "bible/offline/" + normalizedVersion + ".json";
            ClassPathResource resource = new ClassPathResource(path);

            if (!resource.exists()) {
                String fallbackVersion = "ara";
                if (!fallbackVersion.equals(normalizedVersion)) {
                    resource = new ClassPathResource("bible/offline/" + fallbackVersion + ".json");
                }
            }

            if (!resource.exists()) {
                throw new IllegalArgumentException("Offline bible file not found: " + path);
            }

            try (InputStream is = resource.getInputStream()) {
                return mapper.readValue(is, OfflineBibleData.class);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load offline bible version: " + version, ex);
        }
    }

    public List<BibleVersionResponseDTO> getVersions() {
        List<BibleVersionResponseDTO> versions = new ArrayList<>();
        versions.add(new BibleVersionResponseDTO("ara", "Versão local para testes", "Acesso local habilitado", "pt"));
        return versions;
    }

    public List<BibleBookResponseDTO> getBooks(String version) {
        OfflineBibleData data = read(version);
        List<BibleBookResponseDTO> result = new ArrayList<>();

        for (OfflineBookData book : data.books()) {
            result.add(new BibleBookResponseDTO(
                    book.id(),
                    book.name(),
                    book.abbrev(),
                    book.testament()));
        }

        return result;
    }

    public BibleBookResponseDTO getBook(String version, String bookName) {
        OfflineBookData book = findBook(read(version), bookName);
        if (book == null) {
            return null;
        }

        return new BibleBookResponseDTO(
                book.id(),
                book.name(),
                book.abbrev(),
                book.testament());
    }

    public BibleChapterResponseDTO getChapter(String version, String bookName, int chapterNumber) {
        OfflineBibleData data = read(version);
        OfflineBookData book = findBook(data, bookName);

        if (book == null) {
            return null;
        }

        OfflineChapterData chapter = findChapter(book, chapterNumber);
        if (chapter == null) {
            return null;
        }

        List<BibleVerseResponseDTO> verses = new ArrayList<>();
        for (OfflineVerseData verse : chapter.verses()) {
            verses.add(new BibleVerseResponseDTO(
                    verse.reference(),
                    verse.version(),
                    new BibleBookResponseDTO(
                            book.id(),
                            book.name(),
                            book.abbrev(),
                            book.testament()),
                    verse.chapter(),
                    verse.verse(),
                    verse.text()));
        }

        return new BibleChapterResponseDTO(
                chapter.reference(),
                version,
                new BibleBookResponseDTO(
                        book.id(),
                        book.name(),
                        book.abbrev(),
                        book.testament()),
                new BibleChapterInfoDTO(chapter.number(), chapter.verses().size()),
                verses);
    }

    public BibleVerseResponseDTO getVerse(String version, String bookName, int chapterNumber, int verseNumber) {
        OfflineBibleData data = read(version);
        OfflineBookData book = findBook(data, bookName);

        if (book == null) {
            return null;
        }

        OfflineChapterData chapter = findChapter(book, chapterNumber);
        if (chapter == null) {
            return null;
        }

        for (OfflineVerseData verse : chapter.verses()) {
            if (verse.verse() == verseNumber) {
                return new BibleVerseResponseDTO(
                        verse.reference(),
                        verse.version(),
                        new BibleBookResponseDTO(
                                book.id(),
                                book.name(),
                                book.abbrev(),
                                book.testament()),
                        verse.chapter(),
                        verse.verse(),
                        verse.text());
            }
        }

        return null;
    }

    public BibleVerseResponseDTO getRandomVerse(String version) {
        OfflineBibleData data = read(normalizeVersion(version));
        for (OfflineBookData book : data.books()) {
            for (OfflineChapterData chapter : book.chapters()) {
                if (!chapter.verses().isEmpty()) {
                    OfflineVerseData verse = chapter.verses().get(0);
                    return new BibleVerseResponseDTO(
                            verse.reference(),
                            verse.version(),
                            toBook(book),
                            verse.chapter(),
                            verse.verse(),
                            verse.text());
                }
            }
        }
        return null;
    }

    public BibleSearchResponseDTO search(String version, String query, int limit, int offset) {
        OfflineBibleData data = read(version);
        List<BibleSearchResultDTO> results = new ArrayList<>();

        String q = query == null ? "" : query.toLowerCase(Locale.ROOT);

        for (OfflineBookData book : data.books()) {
            for (OfflineChapterData chapter : book.chapters()) {
                for (OfflineVerseData verse : chapter.verses()) {
                    String text = verse.text() == null ? "" : verse.text().toLowerCase(Locale.ROOT);
                    if (q.isBlank() || text.contains(q) || verse.reference().toLowerCase(Locale.ROOT).contains(q)) {
                        results.add(new BibleSearchResultDTO(
                                verse.reference(),
                                new BibleBookResponseDTO(
                                        book.id(),
                                        book.name(),
                                        book.abbrev(),
                                        book.testament()),
                                chapter.number(),
                                verse.verse(),
                                verse.text()));
                    }
                }
            }
        }

        int fromIndex = Math.min(offset, results.size());
        int toIndex = Math.min(fromIndex + limit, results.size());

        return new BibleSearchResponseDTO(query, version, limit, offset, results.subList(fromIndex, toIndex));
    }

    private String normalizeVersion(String version) {
        if (version == null || version.isBlank()) {
            return "ara";
        }

        String normalized = version.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "acf" -> "ara";
            case "acf-pt" -> "ara";
            default -> normalized;
        };
    }

    private OfflineBookData findBook(OfflineBibleData data, String bookName) {
        if (bookName == null) {
            return null;
        }

        String normalized = normalizeKey(bookName);

        for (OfflineBookData book : data.books()) {
            String bookKey = normalizeKey(book.name());
            String abbrevKey = normalizeKey(book.abbrev());

            if (bookKey.contains(normalized) || abbrevKey.contains(normalized)) {
                return book;
            }
        }

        return null;
    }

    private String normalizeKey(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .trim();

        return normalized.replace("\u2019", "'");
    }

    private OfflineChapterData findChapter(OfflineBookData book, int chapterNumber) {
        for (OfflineChapterData chapter : book.chapters()) {
            if (chapter.number() == chapterNumber) {
                return chapter;
            }
        }
        return null;
    }

    private BibleBookResponseDTO toBook(OfflineBookData book) {
        return new BibleBookResponseDTO(
                book.id(),
                book.name(),
                book.abbrev(),
                book.testament());
    }
}