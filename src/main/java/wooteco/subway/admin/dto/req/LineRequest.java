package wooteco.subway.admin.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import wooteco.subway.admin.domain.Line;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.time.LocalTime;

public class LineRequest {
    @NotEmpty
    private String name;
    private String bgColor;
    @Pattern(regexp = "(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)\n", message = "hh:mm:ss 의 형태가 아닙니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;
    @Pattern(regexp = "(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)\n", message = "hh:mm:ss 의 형태가 아닙니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;
    private int intervalTime;

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
        return new Line(name, bgColor, startTime, endTime, intervalTime);
    }
}
