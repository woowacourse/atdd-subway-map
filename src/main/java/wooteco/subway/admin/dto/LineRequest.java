package wooteco.subway.admin.dto;

import java.time.LocalTime;
import java.util.HashSet;

import wooteco.subway.admin.domain.line.Line;

public class LineRequest {
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
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
        return new Line(id, name, startTime, endTime, intervalTime, bgColor, new HashSet<>());
    }
}
