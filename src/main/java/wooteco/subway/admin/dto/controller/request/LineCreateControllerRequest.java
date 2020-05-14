package wooteco.subway.admin.dto.controller.request;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.view.request.LineCreateViewRequest;

import java.time.LocalTime;

public class LineCreateControllerRequest {
	private String name;
	private LocalTime startTime;
	private LocalTime endTime;
	private int intervalTime;
	private String lineColor;

	private LineCreateControllerRequest(String name, LocalTime startTime, LocalTime endTime, int intervalTime,
										String lineColor) {
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.lineColor = lineColor;
	}

	public static LineCreateControllerRequest of(LineCreateViewRequest lineCreateViewRequest) {
		return new LineCreateControllerRequest(lineCreateViewRequest.getName(), lineCreateViewRequest.getStartTime(),
				lineCreateViewRequest.getEndTime(), lineCreateViewRequest.getIntervalTime(), lineCreateViewRequest.getLineColor());
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
