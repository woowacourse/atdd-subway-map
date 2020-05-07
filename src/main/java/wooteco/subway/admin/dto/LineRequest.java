package wooteco.subway.admin.dto;

import java.time.LocalTime;

import wooteco.subway.admin.domain.Line;

public class LineRequest {
    private String name;
    private String bgColor;
    private LocalTime firstTime;
    private LocalTime lastTime;
    private int intervalTime;

    public LineRequest() {
    }

    public String getName() {
        return name;
    }

    public String getBgColor() {
        return bgColor;
    }

    public LocalTime getFirstTime() {
        return firstTime;
    }

    public LocalTime getLastTime() {
        return lastTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public Line toLine() {
        return new Line(name, bgColor, firstTime, lastTime, intervalTime);
    }
}
