package wooteco.subway.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LineRequest {

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "색을 선택해주세요.")
    private String color;

    @NotNull(message = "상행선을 입력해주세요.")
    private Long upStationId;

    @NotNull(message = "하행선을 입력해주세요.")
    private Long downStationId;

    @Min(value = 1, message = "거리는 1이상이어야 합니다.")
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
