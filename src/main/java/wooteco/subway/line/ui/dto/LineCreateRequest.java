package wooteco.subway.line.ui.dto;
import java.beans.ConstructorProperties;

public class LineCreateRequest {
    private final String color;
    private final String name;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    @ConstructorProperties({"color", "name", "upStationId", "downStationId", "distance"})
    public LineCreateRequest(final String color, final String name,
                             final Long upStationId, final Long downStationId,
                             final int distance) {
        this.color = color;
        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
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
