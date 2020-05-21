package wooteco.subway.admin.dto.controller.request;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.view.request.LineViewRequest;

import java.time.LocalTime;

public class LineControllerRequest {
	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String lineColor;

	private LineControllerRequest(String name, LocalTime startTime, LocalTime endTime, int intervalTime,
								  String lineColor) {
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.lineColor = lineColor;
	}

	public static LineControllerRequest of(LineViewRequest lineViewRequest) {
		return new LineControllerRequest(lineViewRequest.getName(), lineViewRequest.getStartTime(),
				lineViewRequest.getEndTime(), lineViewRequest.getIntervalTime(), lineViewRequest.getLineColor());
	}

	public Line toLine() {
		return new Line(name, startTime, endTime, intervalTime, lineColor);
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

	public String getLineColor() {
		return lineColor;
	}
}
