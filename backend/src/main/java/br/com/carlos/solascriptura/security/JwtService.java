package br.com.carlos.solascriptura.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.carlos.solascriptura.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey secretKey;
	private final long expiration;

	public JwtService(
			@Value("${app.security.jwt.secret}") String secret,
			@Value("${app.security.jwt.expiration}") long expiration) {
		this.secretKey = buildSecretKey(secret);
		this.expiration = expiration;
	}

	public String generateToken(User user) {
		Instant now = Instant.now();
		Instant expiresAt = now.plusMillis(expiration);

		return Jwts.builder()
				.subject(user.getEmail())
				.claim("userId", user.getId().toString())
				.claim("role", user.getRole().name())
				.issuedAt(Date.from(now))
				.expiration(Date.from(expiresAt))
				.signWith(secretKey)
				.compact();
	}

	public String extractSubject(String token) {
		return claims(token).getSubject();
	}

	public boolean isTokenValid(String token, String username) {
		String subject = extractSubject(token);
		return subject.equals(username) && claims(token).getExpiration().after(new Date());
	}

	public long getExpiration() {
		return expiration;
	}

	private Claims claims(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private SecretKey buildSecretKey(String secret) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256")
					.digest(secret.getBytes(StandardCharsets.UTF_8));
			return Keys.hmacShaKeyFor(digest);
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA-256 algorithm is not available", ex);
		}
	}
}
