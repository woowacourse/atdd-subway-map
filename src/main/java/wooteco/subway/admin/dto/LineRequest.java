package wooteco.subway.admin.dto;

import java.time.LocalTime;
import java.util.HashSet;

import wooteco.subway.admin.domain.Line;

public class LineRequest {
	private String name;
	private String color;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;

	public Line toLine() {
		return Line.of(name, color, startTime, endTime, intervalTime, new HashSet<>());
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public int getIntervalTime() {
		return intervalTime;
	}
}
