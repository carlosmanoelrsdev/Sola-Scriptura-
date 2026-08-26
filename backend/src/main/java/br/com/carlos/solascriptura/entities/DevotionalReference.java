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

@Entity
@Table(name = "devotional_references")
public class DevotionalReference {

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "devotional_id", nullable = false)
	private Devotional devotional;

	@Column(nullable = false, length = 80)
	private String book;

	@Column(nullable = false)
	private int chapter;

	@Column(nullable = false)
	private int startVerse;

	private Integer endVerse;

	@Column(nullable = false, length = 20)
	private String version;

	@Column(nullable = false)
	private OffsetDateTime createdAt;

	@PrePersist
	void prePersist() {
		id = id == null ? UUID.randomUUID() : id;
		createdAt = createdAt == null ? OffsetDateTime.now() : createdAt;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Devotional getDevotional() {
		return devotional;
	}

	public void setDevotional(Devotional devotional) {
		this.devotional = devotional;
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

	public int getStartVerse() {
		return startVerse;
	}

	public void setStartVerse(int startVerse) {
		this.startVerse = startVerse;
	}

	public Integer getEndVerse() {
		return endVerse;
	}

	public void setEndVerse(Integer endVerse) {
		this.endVerse = endVerse;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
