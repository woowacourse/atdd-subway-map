package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;

import java.time.LocalTime;

public class LineUpdateRequest {
	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;

	public LineUpdateRequest() {
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

	public int getIntervalTime() {
		return intervalTime;
	}

	public Line toLine() {
		return new Line(this.name, this.startTime, this.endTime, this.intervalTime);
	}
}
