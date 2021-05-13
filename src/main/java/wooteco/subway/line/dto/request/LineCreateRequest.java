package wooteco.subway.line.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LineCreateRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String color;
    @NotNull
    private long upStationId;
    @NotNull
    private long downStationId;
    @NotNull
    private int distance;

    public LineCreateRequest() {
    }

    public LineCreateRequest(String name, String color, long upStationId, long downStationId, int distance) {
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

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
