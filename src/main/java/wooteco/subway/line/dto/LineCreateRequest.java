package wooteco.subway.line.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import wooteco.subway.line.domain.Line;
import wooteco.subway.section.domain.Section;

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
    private Integer distance;

    private LineCreateRequest() {
    }

    private LineCreateRequest(String name, String color, Long upStationId, Long downStationId,
        Integer distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toLine() {
        return Line.of(name, color);
    }

    public Section toSection(Long lineId) {
        return Section.of(lineId, upStationId, downStationId, distance);
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

    public boolean isSameStations() {
        return upStationId.equals(downStationId);
    }
}
