package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import wooteco.subway.admin.util.EasyExceptionMaker;

public class Station {

	@Id
	private Long id;
	private String name;
	private LocalDateTime createdAt;

	public Station() {
	}

	public Station(String name) {
		validate(name);
		this.name = name;
		this.createdAt = LocalDateTime.now();
	}

	public Station(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	private void validate(String name) {
		Objects.requireNonNull(name, "역명은 Null일 수 없습니다.");
		EasyExceptionMaker.validateThrowIAE(name.matches(".*[0-9].*"), "역명은 숫자를 포함할 수 없습니다.");
		EasyExceptionMaker.validateThrowIAE(name.contains(" "), "역명은 공백을 포함할 수 없습니다.");
		EasyExceptionMaker.validateThrowIAE(name.isEmpty(), "역명은 공란일 수 없습니다.");
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
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Station station = (Station) o;
		return Objects.equals(id, station.id) &&
			Objects.equals(name, station.name) &&
			Objects.equals(createdAt, station.createdAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, createdAt);
	}

	@Override
	public String toString() {
		return "Station{" +
			"id=" + id +
			", name='" + name + '\'' +
			", createdAt=" + createdAt +
			'}';
	}
}
