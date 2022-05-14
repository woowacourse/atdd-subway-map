package wooteco.subway.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LineRequest {
    @Size(min = 1, max = 255, message = "노선 이름의 길이는 1 이상 255 이하여야 합니다.")
    private String name;
    @Size(min = 1, max = 20, message = "노선 색의 길이는 1 이상 20 이하여야 합니다.")
    private String color;
    @NotNull(message = "상행 종점 id 값이 누락되었습니다.")
    private Long upStationId;
    @NotNull(message = "하행 종점 id 값이 누락되었습니다.")
    private Long downStationId;
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
