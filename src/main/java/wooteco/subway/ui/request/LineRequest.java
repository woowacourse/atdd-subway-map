package wooteco.subway.ui.request;

import wooteco.subway.domain.LineEntity;

public class LineRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;
    private String name;
    private String color;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public LineEntity toEntity() {
        return new LineEntity(name, color);
    }

    public LineEntity toEntity(Long id) {
        return new LineEntity(id, name, color);
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
}
