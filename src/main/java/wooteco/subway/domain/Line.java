package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;

public class Line {

	private static final String NULL_ERROR_MESSAGE = "빈 값이 들어올 수 없습니다.";
	private static final long TEMPORARY_ID = 0L;

	private final Long id;
	private final String name;
	private final String color;

	public Line(String name, String color) {
		this(TEMPORARY_ID, name, color);
	}

	public Line(Long id, String name, String color) {
		List.of(id, name, color)
			.forEach(this::validateNotNull);
		this.id = id;
		this.name = name;
		this.color = color;
	}

	private <T> void validateNotNull(T object) {
		try {
			Objects.requireNonNull(object);
		} catch (NullPointerException exception) {
			throw new IllegalArgumentException(NULL_ERROR_MESSAGE);
		}
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}
}
