package wooteco.subway.admin.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import wooteco.subway.admin.domain.Line;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

public class LineUpdateRequest {
    @JsonAlias("title")
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer intervalTime;

    @JsonAlias("bgColor")
    private String color;

    protected LineUpdateRequest() {
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

    public Integer getIntervalTime() {
        return intervalTime;
    }

    public String getColor() {
        return color;
    }

    public Line toLine() {
        return new Line(this.name, this.startTime, this.endTime, this.intervalTime, this.color);
    }
}
