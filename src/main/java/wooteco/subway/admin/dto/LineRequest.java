package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;

import java.time.LocalTime;

public class LineRequest {
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private String bgColor;

    public LineRequest() {
    }

    public Line toLine() {
        return new Line(title, startTime, endTime, intervalTime, bgColor);
    }

    public static Line toLine(LineRequest lineRequest) {
        return new Line(lineRequest.getTitle(),
                lineRequest.getStartTime(),
                lineRequest.getEndTime(),
                lineRequest.getIntervalTime(),
                lineRequest.getBgColor());
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
