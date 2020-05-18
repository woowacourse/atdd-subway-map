package wooteco.subway.admin.dto.request;

import wooteco.subway.admin.domain.Line;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

public class LineRequest {
    @NotEmpty(message = "노선 이름을 입력해주세요.")
    private String name;
    @NotEmpty(message = "노선 색상을 입력해주세요.")
    private String bgColor;
    @NotNull(message = "첫차 시간을 입력해주세요.")
    private LocalTime startTime;
    @NotNull(message = "막차 시간을 입력해주세요.")
    private LocalTime endTime;
    @NotNull(message = "간격을 입력해주세요.")
    private int intervalTime;

    private LineRequest() {
    }

    public String getName() {
        return name;
    }

    public String getBgColor() {
        return bgColor;
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
        return Line.toLine(name, bgColor, startTime, endTime, intervalTime);
    }
}
