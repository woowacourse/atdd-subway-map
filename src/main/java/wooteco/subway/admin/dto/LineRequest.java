package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.dto.domain.LineDto;

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

    public LineDto toLineDto() {
        return new LineDto.LineDtoBuilder()
                .setName(name)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setIntervalTime(intervalTime)
                .setLineColor(lineColor)
                .build();
    }

    public Line toLine() {
        return new Line(name, startTime, endTime, intervalTime, lineColor);
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
}
