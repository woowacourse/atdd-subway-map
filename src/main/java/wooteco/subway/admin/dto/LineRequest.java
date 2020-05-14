package wooteco.subway.admin.dto;

import java.time.LocalTime;
import java.util.HashSet;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import wooteco.subway.admin.domain.line.Line;

public class LineRequest {
    private Long id;
    @NotBlank(message = "이름은 필수 입력 사항입니다.")
    private String name;

    private LocalTime startTime;
    private LocalTime endTime;

    @Min(value = 1, message = "배차 간격은 1분 이상 이어야 합니다.")
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
        return new Line(id, name, startTime, endTime, intervalTime, bgColor, new HashSet<>());
    }
}
