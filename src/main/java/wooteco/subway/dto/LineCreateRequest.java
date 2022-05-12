package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;

public class LineCreateRequest {

    @NotBlank(message = "노선 이름은 빈 값일 수 없습니다.")
    private final String name;
    @NotBlank(message = "노선 색상은 빈 값일 수 없습니다.")
    private final String color;
    @NotBlank(message = "시점은 빈 값일 수 없습니다.")
    private final Long upStationId;
    @NotBlank(message = "종점은 빈 값일 수 없습니다.")
    private final Long downStationId;
    @NotBlank(message = "노선 거리는 빈 값일 수 없습니다.")
    private final int distance;

    private LineCreateRequest() {
        this(null, null, null, null, 0);
    }

    public LineCreateRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
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
