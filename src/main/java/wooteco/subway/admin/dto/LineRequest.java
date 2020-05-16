package wooteco.subway.admin.dto;

import java.time.LocalTime;

import wooteco.subway.admin.domain.Line;

public class LineRequest {
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String backgroundColor;

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

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public Line toLine() {
        return new Line(title, startTime, endTime, intervalTime, backgroundColor);
    }

    @Override
    public String toString() {
        return "LineRequest{" +
            "name='" + title + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", intervalTime=" + intervalTime +
            ", bgColor='" + backgroundColor + '\'' +
            '}';
    }
}
