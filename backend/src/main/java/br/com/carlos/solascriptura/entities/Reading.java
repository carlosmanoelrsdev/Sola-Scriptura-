package br.com.carlos.solascriptura.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
		name = "readings",
		uniqueConstraints = @UniqueConstraint(
				name = "uk_reading_user_version_book_chapter",
				columnNames = {"user_id", "version", "book", "chapter"}))
public class Reading {

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 20)
	private String version;

	@Column(nullable = false, length = 80)
	private String book;

	@Column(nullable = false)
	private int chapter;

	@Column(nullable = false)
	private OffsetDateTime readAt;

	@PrePersist
	void prePersist() {
		id = id == null ? UUID.randomUUID() : id;
		readAt = readAt == null ? OffsetDateTime.now() : readAt;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getBook() {
		return book;
	}

	public void setBook(String book) {
		this.book = book;
	}

	public int getChapter() {
		return chapter;
	}

	public void setChapter(int chapter) {
		this.chapter = chapter;
	}

	public OffsetDateTime getReadAt() {
		return readAt;
	}

	public void setReadAt(OffsetDateTime readAt) {
		this.readAt = readAt;
	}
}
