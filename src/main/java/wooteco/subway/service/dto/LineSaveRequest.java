package wooteco.subway.service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class LineSaveRequest {

    @NotBlank(message = "이름 값을 입력해주세요.")
    private String name;

    @NotBlank(message = "색상 값을 입력해주세요.")
    private String color;

    @NotNull(message = "상행역을 선택해주세요.")
    private Long upStationId;

    @NotNull(message = "하행역을 선택해주세요.")
    private Long downStationId;

    @Positive(message = "구간 거리는 1 이상이어야 합니다.")
    private int distance;

    private LineSaveRequest() {
    }

    public LineSaveRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
