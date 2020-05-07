package wooteco.subway.admin.dto;

import java.time.LocalTime;

import wooteco.subway.admin.domain.Line;

public class LineRequest {
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
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

    @Override
    public String toString() {
        return "LineRequest{" +
            "name='" + title + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", intervalTime=" + intervalTime +
            ", bgColor='" + bgColor + '\'' +
            '}';
    }
}
