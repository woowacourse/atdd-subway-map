package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

public class LineRequest {
    @NotNull
    private String title;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    @NotNull
    private int intervalTime;
    @NotNull
    private String bgColor;

    public LineRequest() {
    }

    public String getTitle() {
        return title;
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
        return new Line(title, startTime, endTime, intervalTime, bgColor);
    }
}
