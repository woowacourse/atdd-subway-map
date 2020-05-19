package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

public class LineRequest {
    @NotBlank(message = "노선 이름이 비었습니다.")
    private String name;
    @NotNull(message = "첫차 시간이 비었습니다.")
    private LocalTime startTime;
    @NotNull(message = "막차 시간이 비었습니다.")
    private LocalTime endTime;
    @Min(value = 0, message = "간격 시간이 올바르지 않습니다.")
    private int intervalTime;
    @NotBlank(message = "노선 색을 선택해 주세요.")
    private String bgColor;

    public LineRequest() {
    }

    public LineRequest(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
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
