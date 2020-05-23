package wooteco.subway.admin.dto;

import java.time.LocalTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;

import wooteco.subway.admin.domain.Line;

public class LineRequest {
    @NotBlank
    private String name;

    @NotNull
    @DateTimeFormat(pattern = "hh:mm:ss")
    private LocalTime startTime;

    @NotNull
    @DateTimeFormat(pattern = "hh:mm:ss")
    private LocalTime endTime;

    @Positive
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
        return new Line(name, startTime, endTime, intervalTime, bgColor);
    }
}
