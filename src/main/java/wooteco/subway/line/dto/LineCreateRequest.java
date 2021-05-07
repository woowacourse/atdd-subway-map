package wooteco.subway.line.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LineCreateRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String color;
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private int distance;

    public LineCreateRequest() {
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

    public boolean isSameStations() {
        return upStationId.equals(downStationId);
    }
}
