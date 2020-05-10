package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalTime;

public class LineRequest {
    @NotBlank(message = "이름을 작성해주세요")
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    @Positive(message = "0 이상의 숫자를 입력해주세요")
    private int intervalTime;
    @NotBlank(message = "노선 색을 결정해주세요.")
    private String lineColor;

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

    public String getLineColor() {
        return lineColor;
    }

    public Line toLine() {
        return new Line(name, startTime, endTime, intervalTime, lineColor);
    }
}
