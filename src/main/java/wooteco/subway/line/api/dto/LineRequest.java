package wooteco.subway.line.api.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class LineRequest {

    @Size(min = 2, message = "노선 이름은 최소 2글자 이상만 가능합니다.")
    private String name;

    @NotEmpty(message = "노선 색을 지정해야합니다.")
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this(name, color, null, null, 0);
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
