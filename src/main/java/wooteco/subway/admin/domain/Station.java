package wooteco.subway.admin.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;

public class Station {
	@Id
	private Long id;
	@Embedded.Nullable
	private StationName name;
	private LocalDateTime createdAt;

	public Station() {
	}

	public Station(Long id, String name) {
		this.id = id;
		this.name = new StationName(name);
	}

	public Station(String name) {
		this.name = new StationName(name);
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name.getName();
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
