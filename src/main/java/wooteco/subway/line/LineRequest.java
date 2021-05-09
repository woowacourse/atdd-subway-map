package wooteco.subway.line;

import javax.validation.constraints.NotBlank;

public class LineRequest {
    @NotBlank(message = "노선 이름을 입력하셔야 합니다.")
    private String name;
    @NotBlank(message = "색상을 지정하셔야 합니다.")
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    private LineRequest() {
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
