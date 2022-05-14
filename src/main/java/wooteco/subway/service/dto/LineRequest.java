package wooteco.subway.service.dto;

import javax.validation.constraints.NotBlank;

public class LineRequest {

    @NotBlank(message = "이름 값을 입력해주세요.")
    private String name;

    @NotBlank(message = "색상 값을 입력해주세요.")
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
