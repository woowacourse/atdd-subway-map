package wooteco.subway.admin.dto;

import java.time.LocalTime;
import wooteco.subway.admin.domain.Line;

public class LineRequest {

    private String name;
    private String color;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;

    public LineRequest() {
    }

    public Line toLine() {
        return new Line(name, color, startTime, endTime, intervalTime);
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

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "LineRequest{" +
            "name='" + name + '\'' +
            ", color='" + color + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", intervalTime=" + intervalTime +
            '}';
    }
}
