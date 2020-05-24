package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.annotation.Id;

public class Station {
	@Id
	private Long id;
	private String name;
	private LocalDateTime createdAt;

	public Station() {
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Station station = (Station)o;
		return Objects.equals(name, station.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
