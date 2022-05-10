package wooteco.subway.dto;

public class LineRequestV2 {

    private final String name;
    private final String color;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public LineRequestV2(final String name, final String color, final Long upStationId, final Long downStationId,
                         final int distance) {
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
