package wooteco.subway.admin.dto;

import java.time.LocalTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import wooteco.subway.admin.domain.Line;

public class LineRequest {
	@NotNull
	@NotBlank
	private String name;

	@NotNull
	@DateTimeFormat(pattern = "HH:mm:ss")
	private LocalTime startTime;

	@NotNull
	@DateTimeFormat(pattern = "HH:mm:ss")
	private LocalTime endTime;

	@Min(1)
	private int intervalTime;

	@NotNull
	@NotBlank
	private String bgColor;

	private LineRequest() {
	}

	public Line toLine() {
		return new Line(null, name, startTime, endTime, intervalTime, bgColor);
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
}
