package wooteco.subway.controller.dto.response.line;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

public class LineCreateResponseDto {
    private Long id;
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public LineCreateResponseDto() {
    }

    public LineCreateResponseDto(Long id, String name, String color,
        Long upStationId, Long downStationId, Integer distance) {

        this.id = id;
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public LineCreateResponseDto(Line line, Section section) {
        this(line.getId(), line.getName(), line.getColor(),
            section.getUpStationId(), section.getDownStationId(), section.getDistance());
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
