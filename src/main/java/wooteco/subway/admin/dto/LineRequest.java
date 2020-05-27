package wooteco.subway.admin.dto;

import java.time.LocalTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Range;

import wooteco.subway.admin.domain.Line;

public class LineRequest {
    @NotEmpty(message = "Line title 값이 비었습니다.")
    @Pattern(regexp = "\\S", message = "공백이 불가능합다")
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    @Range(min = 1, message = "최소 배차 간격은 1 이상입니다.")
    private int intervalTime;
    private String backgroundColor;

    public LineRequest() {
    }

    public String getTitle() {
        return title;
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

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public Line toLine() {
        return new Line(title, startTime, endTime, intervalTime, backgroundColor);
    }

    @Override
    public String toString() {
        return "LineRequest{" +
            "name='" + title + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", intervalTime=" + intervalTime +
            ", bgColor='" + backgroundColor + '\'' +
            '}';
    }
}
