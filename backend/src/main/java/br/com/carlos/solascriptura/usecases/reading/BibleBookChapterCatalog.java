package br.com.carlos.solascriptura.usecases.reading;

import java.util.Map;

final class BibleBookChapterCatalog {

	static final int TOTAL_CHAPTERS = 1189;

	private static final Map<String, Integer> CHAPTERS_BY_BOOK = Map.ofEntries(
			Map.entry("gn", 50), Map.entry("ex", 40), Map.entry("lv", 27), Map.entry("nm", 36),
			Map.entry("dt", 34), Map.entry("js", 24), Map.entry("jz", 21), Map.entry("rt", 4),
			Map.entry("1sm", 31), Map.entry("2sm", 24), Map.entry("1rs", 22), Map.entry("2rs", 25),
			Map.entry("1cr", 29), Map.entry("2cr", 36), Map.entry("ed", 10), Map.entry("ne", 13),
			Map.entry("et", 10), Map.entry("job", 42), Map.entry("sl", 150), Map.entry("pv", 31),
			Map.entry("ec", 12), Map.entry("ct", 8), Map.entry("is", 66), Map.entry("jr", 52),
			Map.entry("lm", 5), Map.entry("ez", 48), Map.entry("dn", 12), Map.entry("os", 14),
			Map.entry("jl", 3), Map.entry("am", 9), Map.entry("ob", 1), Map.entry("jn", 4),
			Map.entry("mq", 7), Map.entry("na", 3), Map.entry("hc", 3), Map.entry("sf", 3),
			Map.entry("ag", 2), Map.entry("zc", 14), Map.entry("ml", 4), Map.entry("mt", 28),
			Map.entry("mc", 16), Map.entry("lc", 24), Map.entry("jo", 21), Map.entry("atos", 28),
			Map.entry("rm", 16), Map.entry("1co", 16), Map.entry("2co", 13), Map.entry("gl", 6),
			Map.entry("ef", 6), Map.entry("fp", 4), Map.entry("cl", 4), Map.entry("1ts", 5),
			Map.entry("2ts", 3), Map.entry("1tm", 6), Map.entry("2tm", 4), Map.entry("tt", 3),
			Map.entry("fm", 1), Map.entry("hb", 13), Map.entry("tg", 5), Map.entry("1pe", 5),
			Map.entry("2pe", 3), Map.entry("1jo", 5), Map.entry("2jo", 1), Map.entry("3jo", 1),
			Map.entry("jd", 1), Map.entry("ap", 22));

	private BibleBookChapterCatalog() {
	}

	static int chapterCount(String book) {
		return CHAPTERS_BY_BOOK.getOrDefault(book.toLowerCase(), 0);
	}

	static Map<String, Integer> chaptersByBook() {
		return CHAPTERS_BY_BOOK;
	}
}
