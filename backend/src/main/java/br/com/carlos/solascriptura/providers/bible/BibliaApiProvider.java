package br.com.carlos.solascriptura.providers.bible;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class BibliaApiProvider implements BibleProvider {

	@Override
	public List<String> getVersions() {
		return List.of();
	}
}
