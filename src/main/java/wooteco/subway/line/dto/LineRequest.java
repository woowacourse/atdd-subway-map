package wooteco.subway.line.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LineRequest {
    @NotBlank(message = "이름은 비어있을 수 없습니다.")
    private String name;

    @NotBlank(message = "색깔은 비어있을 수 없습니다.")
    private String color;

    @NotNull(message = "노선 추가시 상행선은 비어있을 수 없습니다.")
    private Long upStationId;

    @NotNull(message = "노선 추가시 하행선은 비어있을 수 없습니다.")
    private Long downStationId;

    @NotNull(message = "노선 추가시 상행선과 하행선의 거리는 비어있을 수 없습니다.")
    private Integer distance;

    public LineRequest() {
    }

    public LineRequest(final String name, final String color, final Long upStationId, final Long downStationId, final Integer distance) {
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

    public Integer getDistance() {
        return distance;
    }
}
