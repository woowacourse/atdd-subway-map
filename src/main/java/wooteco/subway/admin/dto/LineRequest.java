package wooteco.subway.admin.dto;

import java.time.LocalTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import wooteco.subway.admin.domain.Line;

public class LineRequest {
    @NotBlank(message = "노선의 이름을 작성해주세요!")
    private String name;
    @NotNull(message = "첫차 시간을 작성해주세요!")
    private LocalTime startTime;
    @NotNull(message = "막차 시간을 작성해주세요!")
    private LocalTime endTime;
    @NotNull(message = "간격을 작성해주세요!")
    private Integer intervalTime;
    @NotBlank(message = "노선의 배경색을 작성해주세요!")
    private String backgroundColor;

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

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public Line toLine() {
        return new Line(name, startTime, endTime, intervalTime, backgroundColor);
    }
}
