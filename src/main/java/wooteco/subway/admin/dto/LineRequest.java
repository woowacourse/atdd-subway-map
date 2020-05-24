package wooteco.subway.admin.dto;

import wooteco.subway.admin.domain.Line;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

public class LineRequest {
    @NotBlank(message = "노선 이름이 비어있습니다.")
    private String title;
    @NotNull(message = "첫차 시간이 비어있습니다.")
    private LocalTime startTime;
    @NotNull(message = "막차 시간이 비어있습니다.")
    private LocalTime endTime;
    @Min(value = 1, message = "1 이상의 간격 시간을 입력해주세요.")
    private int intervalTime;
    @NotBlank(message = "노선 색상을 선택하지 않았습니다.")
    private String bgColor;

    public LineRequest() {
    }

    public LineRequest(@NotBlank(message = "노선 이름이 비어있습니다.") String title, @NotNull(message = "첫차 시간이 비어있습니다.") LocalTime startTime, @NotNull(message = "막차 시간이 비어있습니다.") LocalTime endTime, @Min(value = 1, message = "1 이상의 간격 시간을 입력해주세요.") int intervalTime, @NotBlank(message = "노선 색상을 선택하지 않았습니다.") String bgColor) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
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

    public String getBgColor() {
        return bgColor;
    }
}
