package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;

import javax.validation.constraints.NotEmpty;
import java.time.LocalTime;

public class LineRequest {
    @NotEmpty
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    @NotEmpty
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
