package wooteco.subway.admin.line.service.dto.line;

import com.fasterxml.jackson.annotation.JsonAlias;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

public class LineCreateRequest {
    @JsonAlias("title")
    @NotBlank(message = "노선 이름값이 비어있습니다.")
    private String name;
    @NotNull(message = "첫차 시간이 비어있습니다.")
    private LocalTime startTime;
    @NotNull(message = "막차 시간이 비어있습니다.")
    private LocalTime endTime;
    @Min(value = 1, message = "배차 시간은 1이상 입력해 주십시오.")
    private Integer intervalTime;
    @NotNull(message = "노선 색상값이 비어있습니다.")
    private String bgColor;

    private LineCreateRequest() {
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

    public String getBgColor() {
        return bgColor;
    }

}
