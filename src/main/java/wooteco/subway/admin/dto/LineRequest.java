package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

public class LineRequest {
    @NotEmpty(message = "노선 이름을 입력해야 한다.")
    private String name;
    @NotEmpty(message = "노선 색상을 입력해야 한다.")
    private String color;
    @NotNull(message = "첫차 시간을 입력해야 한다.")
    private LocalTime startTime;
    @NotNull(message = "막차 시간을 입력해야 한다.")
    private LocalTime endTime;
    @NotNull(message = "간격을 입력해야 한다.")
    private Integer intervalTime;

    public LineRequest() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
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

    public Line toLine() {
        return new Line(name, color, startTime, endTime, intervalTime);
    }
}
