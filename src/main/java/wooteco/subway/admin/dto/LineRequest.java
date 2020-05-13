package wooteco.subway.admin.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalTime;

public class LineRequest {
    @NotBlank(message = "이름은 빈 값이 될 수 없습니다.")
    private String name;
    @NotNull(message = "첫차 시간을 입력해주세요.")
    private LocalTime startTime;
    @NotNull(message = "막차 시간을 입력해주세요.")
    private LocalTime endTime;
    @PositiveOrZero(message = "간격을 정확히 입력해주세요.")
    private int intervalTime;
    @NotNull(message = "색을 선택해주세요.")
    private String bgColor;

    public LineRequest() {
    }

    public LineRequest(String name, LocalTime startTime, LocalTime endTime, int intervalTime, String bgColor) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.bgColor = bgColor;
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
}
