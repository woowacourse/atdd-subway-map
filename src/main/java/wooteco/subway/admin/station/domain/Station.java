package wooteco.subway.admin.station.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

public class Station {

	@Id
	@Column("id")
	private Long id;

	@Column("name")
	private String name;

	@Column("created_at")
	@CreatedDate
	private LocalDateTime createdAt;

	public Station() {
	}

	public Station(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Station(String name) {
		this.name = name;
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
