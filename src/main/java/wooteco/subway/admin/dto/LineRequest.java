package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalTime;

public class LineRequest {

    @NotEmpty
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    @Positive
    private int intervalTime;
    @NotNull
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
