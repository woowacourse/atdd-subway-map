package wooteco.subway.line;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class LineRequest {
    @NotBlank(message = "노선 이름을 입력하셔야 합니다.")
    private String name;
    @NotBlank(message = "색상을 지정하셔야 합니다.")
    private String color;
    @Positive
    @NotNull
    private Long upStationId;
    @Positive
    @NotNull
    private Long downStationId;
    @Positive
    @NotNull
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
