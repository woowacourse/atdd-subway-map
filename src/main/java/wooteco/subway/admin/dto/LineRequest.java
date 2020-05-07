package wooteco.subway.admin.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import wooteco.subway.admin.domain.Line;

public class LineRequest {
    private String name;
    private String bgColor;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;
    private int intervalTime;

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
        return new Line(name, bgColor, startTime, endTime, intervalTime);
    }
}
