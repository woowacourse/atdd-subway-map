package wooteco.subway.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import wooteco.subway.admin.domain.Line;

import javax.validation.constraints.Pattern;
import java.time.LocalTime;

public class LineCreateRequest {
    private String name;
    @Pattern(regexp = "(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)\n", message = "hh:mm:ss 의 형태가 아닙니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;
    @Pattern(regexp = "(?:[01]\\d|2[0123]):(?:[012345]\\d):(?:[012345]\\d)\n", message = "hh:mm:ss 의 형태가 아닙니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;
    private int intervalTime;
    private String lineColor;

    public LineCreateRequest() {
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

    public String getLineColor() {
        return lineColor;
    }

    public Line toLine() {
        return new Line(name, startTime, endTime, intervalTime, lineColor);
    }
}
