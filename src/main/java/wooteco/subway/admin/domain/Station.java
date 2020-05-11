package wooteco.subway.admin.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

public class Station {
	@Id
	private final Long id;
	private final String name;
	private final LocalDateTime createdAt;

	Station(Long id, String name, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.createdAt = createdAt;
	}

	public static Station of(String name) {
		return new Station(null, name, LocalDateTime.now());
	}

	public Station withId(Long id) {
		return new Station(id, this.name, this.createdAt);
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
