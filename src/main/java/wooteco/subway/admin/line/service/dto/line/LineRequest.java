package wooteco.subway.admin.line.service.dto.line;

import java.time.LocalTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import wooteco.subway.admin.line.domain.line.Line;

public class LineRequest {

	@NotBlank
	private String name;

	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime startTime;

	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime endTime;

	@NotNull
	private Integer intervalTime;

	@NotBlank
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

	public Integer getIntervalTime() {
		return intervalTime;
	}

	public String getBgColor() {
		return bgColor;
	}

	public Line toLine() {
		return new Line(name, startTime, endTime, intervalTime, bgColor);
	}

}
