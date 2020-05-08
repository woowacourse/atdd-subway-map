package wooteco.subway.admin.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import wooteco.subway.admin.domain.Line;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

public class LineUpdateRequest {
    @NotEmpty(message = "수정하려는 이름 값이 비어있습니다.")
    @JsonAlias("title")
    private String name;
    @NotNull(message = "수정하려는 첫차 시간이 비어있습니다.")
    private LocalTime startTime;
    @NotNull(message = "수정하려는 막차 시간이 비어있습니다.")
    private LocalTime endTime;
    @NotNull(message = "수정하려는 배차 간격 값이 비어있습니다.")
    private Integer intervalTime;

    @JsonAlias("bgColor")
    @NotEmpty(message = "수정하려는 노선 색상 값이 비어있습니다.")
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
