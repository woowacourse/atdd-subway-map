package wooteco.subway.admin.dto;

import java.time.LocalTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import wooteco.subway.admin.domain.Line;

public class LineRequest {
	@NotNull(message = "null값은 허용되지 않습니다.")
	@NotBlank(message = "노선명을 입력해주세요.")
	private String name;

	@NotNull(message = "출발시간을 입력해주세요.")
	@DateTimeFormat(pattern = "HH:mm:ss")
	private LocalTime startTime;

	@NotNull(message = "도착시간을 입력해주세요.")
	@DateTimeFormat(pattern = "HH:mm:ss")
	private LocalTime endTime;

	@Min(value = 1, message = "배차 간격에 양수를 입력해주세요.")
	private int intervalTime;

	@NotNull(message = "null값은 허용되지 않습니다.")
	@NotBlank(message = "노선의 배경색을 입력해주세요.")
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
