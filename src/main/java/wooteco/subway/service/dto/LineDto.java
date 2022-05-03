package wooteco.subway.service.dto;

import wooteco.subway.domain.Line;

public class LineDto {

	private final Long id;
	private final String name;
	private final String color;

	public LineDto(Long id, String name, String color) {
		this.id = id;
		this.name = name;
		this.color = color;
	}

	public static LineDto from(Line line) {
		return new LineDto(line.getId(), line.getName(), line.getColor());
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
