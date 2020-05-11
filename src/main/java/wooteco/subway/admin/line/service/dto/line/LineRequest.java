package wooteco.subway.admin.line.service.dto.line;

import java.time.LocalTime;

import wooteco.subway.admin.line.domain.line.Line;

public class LineRequest {

	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String bgColor;

	public LineRequest() {
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

	public String getBgColor() {
		return bgColor;
	}

	public Line toLine() {
		return new Line(name, startTime, endTime, intervalTime, bgColor);
	}

}
