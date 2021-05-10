package wooteco.subway.line.dto.response;

import wooteco.subway.section.dto.response.SectionCreateResponse;

public class LineCreateResponse {
    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public LineCreateResponse() {
    }

    public LineCreateResponse(LineResponse line, SectionCreateResponse section) {
        this(line.getId(), line.getName(), line.getColor(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public LineCreateResponse(Long id, String name, String color, Long upStationId, Long downStationId, Integer distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
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
