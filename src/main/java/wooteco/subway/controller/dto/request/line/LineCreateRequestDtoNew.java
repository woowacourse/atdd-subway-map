package wooteco.subway.controller.dto.request.line;

public class LineCreateRequestDtoNew {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public LineCreateRequestDtoNew() {
    }

    public LineCreateRequestDtoNew(String name, String color,
        Long upStationId, Long downStationId, Integer distance) {

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

    public Integer getDistance() {
        return distance;
    }
}
