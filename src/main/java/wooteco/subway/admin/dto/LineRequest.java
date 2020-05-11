package wooteco.subway.admin.dto;

import java.time.LocalTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import wooteco.subway.admin.domain.Line;

public class LineRequest {
	@NotBlank
	private String name;
	@NotBlank
	private String color;
	private LocalTime startTime;
	private LocalTime endTime;
	@Min(1)
	private int intervalTime;

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

	public String getColor() {
		return color;
	}

	public Line toLine() {
		return new Line(name, color, startTime, endTime, intervalTime);
	}

	@Override
	public String toString() {
		return "LineRequest{" +
			"name='" + name + '\'' +
			", color='" + color + '\'' +
			", startTime=" + startTime +
			", endTime=" + endTime +
			", intervalTime=" + intervalTime +
			'}';
	}
}
