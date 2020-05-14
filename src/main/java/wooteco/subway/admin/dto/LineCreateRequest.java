package wooteco.subway.admin.dto;

import java.time.LocalTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAlias;
import wooteco.subway.admin.domain.Line;

public class LineCreateRequest {
	@JsonAlias("title")
	@NotBlank(message = "노선 이름값이 비어있습니다.")
	private String name;
	@NotNull(message = "첫차 시간이 비어있습니다.")
	private LocalTime startTime;
	@NotNull(message = "막차 시간이 비어있습니다.")
	private LocalTime endTime;
	@NotNull(message = "배차 시간이 비어있습니다.")
	private Integer intervalTime;
	@NotNull(message = "노선 색상값이 비어있습니다.")
	private String bgColor;

	private LineCreateRequest() {
	}

	public LineCreateRequest(String name,
		LocalTime startTime,
		LocalTime endTime,
		Integer intervalTime,
		String bgColor) {
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.intervalTime = intervalTime;
		this.bgColor = bgColor;
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

	public String getBgColor() {
		return bgColor;
	}

	public Line toLine() {
		return new Line(this.name, this.startTime, this.endTime, this.intervalTime, this.bgColor);
	}
}
