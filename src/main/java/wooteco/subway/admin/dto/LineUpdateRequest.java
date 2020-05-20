package wooteco.subway.admin.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import wooteco.subway.admin.domain.Line;

public class LineUpdateRequest {
	@JsonAlias("title")
	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private Integer intervalTime;

	@JsonAlias("bgColor")
	private String color;

	private LineUpdateRequest() {
	}

	public LineUpdateRequest(String name, LocalTime startTime, LocalTime endTime, Integer intervalTime,
		String color) {
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public Integer getIntervalTime() {
		return intervalTime;
	}

	public String getColor() {
		return color;
	}

	public Line toLine() {
		return Line.of(this.name, this.startTime, this.endTime, this.intervalTime, this.color);
	}
}
