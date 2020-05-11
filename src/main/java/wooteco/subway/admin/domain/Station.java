package wooteco.subway.admin.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

public class Station {
	@Id
	private Long id;
	private String name;
	private LocalDateTime createdAt;

	public Station() {
	}

	public Station(Long id, String name, LocalDateTime createdAt) {
		this.name = name;
		this.createdAt = createdAt;
	}

	public Station(String name) {
		this(null, name, LocalDateTime.now());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
