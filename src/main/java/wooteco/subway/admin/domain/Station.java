package wooteco.subway.admin.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

public class Station {

	@Id
	@Column("id")
	private Long id;

	@Column("name")
	private String name;

	@Column("created_at")
	private LocalDateTime createdAt;

	public Station() {
	}

	public Station(Long id, String name) {
		this.id = id;
		this.name = name;
		this.createdAt = LocalDateTime.now();
	}

	public Station(String name) {
		this.name = name;
		this.createdAt = LocalDateTime.now();
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
