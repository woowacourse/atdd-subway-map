package wooteco.subway.admin.dto;

import java.time.LocalTime;

import wooteco.subway.admin.domain.Line;

public class LineRequest {
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String bgColor;

    public LineRequest() {
    }

    public LineRequest(String name, LocalTime startTime, LocalTime endTime, int intervalTime,
        String bgColor) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
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
