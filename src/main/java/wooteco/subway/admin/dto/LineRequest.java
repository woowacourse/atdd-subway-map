package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LineRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String color;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    @NotNull
    private Integer intervalTime;

    public LineRequest() {
    }

    public LineRequest(String name, String color, String startTime, String endTime, Integer intervalTime) {
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
        return new Line(name, color, startTime, endTime, intervalTime);
    }
}
