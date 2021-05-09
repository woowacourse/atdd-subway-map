package wooteco.subway.dto.line.request;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class LineInsertRequest {
    @NotBlank
    @Pattern(regexp = "^[가-힣|A-Z|a-z| 0-9]*선$")
    private String name;
    @NotBlank
    private String color;
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private int distance;

    public LineInsertRequest() {
    }

    public LineInsertRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
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

    public Line toLineEntity() {
        return new Line(color, name);
    }

    public Section toSectionEntity(Long lineId) {
        return new Section(lineId, upStationId, downStationId, distance);
    }
}
