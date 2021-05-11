package wooteco.subway.controller.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LineRequest {

    @NotNull(message = "이름을 입력해주세요.")
    @NotEmpty(message = "이름은 공백을 허용하지 않습니다.")
    private String name;
    @NotNull(message = "색상을 선택해주세요.")
    private String color;
    @NotNull(message = "상행역을 선택해주세요.")
    private Long upStationId;
    @NotNull(message = "하행역을 선택해주세요.")
    private Long downStationId;
    @Min(value = 1, message = "거리는 1 이상이어야합니다.")
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
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
