package wooteco.subway.admin.dto;

import java.time.LocalTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import wooteco.subway.admin.domain.Line;

public class LineRequest {
    @NotEmpty(message = "노선 이름이 입력되지 않았습니다.")
    private String name;
    @NotNull(message = "첫차 시간이 입력되지 않았습니다.")
    private LocalTime startTime;
    @NotNull(message = "막차 시간이 입력되지 않았습니다.")
    private LocalTime endTime;
    @Min(value = 1, message = "배차 시간은 최소 1 이상 입력해야 합니다.")
    private int intervalTime;
    @NotNull(message = "노선 색깔이 입력되지 않았습니다.")
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
