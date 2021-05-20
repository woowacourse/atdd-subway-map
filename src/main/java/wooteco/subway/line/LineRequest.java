package wooteco.subway.line;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


public class LineRequest {

    @NotEmpty(message = "노선 이름은 필수입니다.")
    private String name;
    @NotEmpty(message = "노선 색상을 필수입니다.")
    private String color;
    @NotNull(message = "상행 이름은 필수입니다.")
    private Long upStationId;
    @NotNull(message = "하행 이름은 필수입니다.")
    private Long downStationId;
    @Min(value = 1, message = "거리 정보는 1 이상이여야 합니다.")
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId,
        int distance) {
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
