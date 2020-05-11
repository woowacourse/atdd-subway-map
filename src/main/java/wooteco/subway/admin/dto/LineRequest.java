package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LineRequest {
    private String name;
    private String color;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer intervalTime;

    public LineRequest() {
    }

    public LineRequest(final String name, final String color, final String startTime, final String endTime, final int intervalTime) {
        this.name = name;
        this.color = color;
        this.startTime = LocalTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_TIME);
        this.endTime = LocalTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_TIME);
        this.intervalTime = intervalTime;
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

    public int getIntervalTime() {
        return intervalTime;
    }

    public Line toLine() {
        if (name.isEmpty() | color.isEmpty() | startTime == null | endTime == null | intervalTime == null) {
            throw new IllegalArgumentException("요청이 올바르지 않습니다.");
        }
        return new Line(name, color, startTime, endTime, intervalTime);
    }
}
