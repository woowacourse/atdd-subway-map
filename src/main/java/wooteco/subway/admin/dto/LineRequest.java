package wooteco.subway.admin.dto;

import java.time.LocalTime;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import wooteco.subway.admin.domain.Line;

public class LineRequest {
    @Pattern(regexp = "^\\S+$", message = "이름은 공백을 포함할 수 없다.")
    private String name;
    @NotEmpty(message = "색상을 입력해야 한다.")
    private String color;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;
    @Positive(message = "간격은 1 미만의 값이 올 수 없다.")
    private Integer intervalTime;

    public LineRequest() {
    }

    public LineRequest(String name, String color, LocalTime startTime, LocalTime endTime,
            Integer intervalTime) {
        this.name = name;
        this.color = color;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public Integer getIntervalTime() {
        return intervalTime;
    }

    public Line toLine() {
        return new Line(name, color, startTime, endTime, intervalTime);
    }
}
