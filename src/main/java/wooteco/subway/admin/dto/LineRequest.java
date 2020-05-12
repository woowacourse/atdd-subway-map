package wooteco.subway.admin.dto;

import java.time.LocalTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import wooteco.subway.admin.domain.Line;

public class LineRequest {

    @NotBlank(message = "이름은 필수입력 항목입니다.")
    private String name;

    @NotNull(message = "첫차시간은 필수입력 항목입니다.")
    private LocalTime startTime;

    @NotNull(message = "막차시간은 필수입력 항목입니다.")
    private LocalTime endTime;

    @Min(value = 1, message = "배차간격은 필수입력 항목입니다.")
    private int intervalTime;

    @NotBlank(message = "노선색깔은 필수입력 항목입니다.")
    private String bgColor;

    public LineRequest() {
    }

    public LineRequest(String name, LocalTime startTime, LocalTime endTime, int intervalTime,
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
