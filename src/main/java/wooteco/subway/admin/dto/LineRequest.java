package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LineRequest {
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;

    public LineRequest() {
    }

    public LineRequest(final String name, final String startTime, final String endTime, final int intervalTime) {
        this.name = name;
        this.startTime = LocalTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_TIME);
        this.endTime = LocalTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_TIME);
        this.intervalTime = intervalTime;
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

    public Line toLine() {
        return new Line(name, startTime, endTime, intervalTime);
    }
}
