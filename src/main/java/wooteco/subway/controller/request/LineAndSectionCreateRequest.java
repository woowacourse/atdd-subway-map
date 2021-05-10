package wooteco.subway.controller.request;

import wooteco.subway.domain.Line;
import wooteco.subway.service.dto.SimpleSection;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class LineAndSectionCreateRequest {
    @Pattern(regexp = "^[가-힣|0-9]*선$")
    private String name;
    @NotNull
    private String color;
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private int distance;

    public LineAndSectionCreateRequest() {
    }

    public LineAndSectionCreateRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
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

    public Line toLine() {
        return new Line(color, name);
    }

    public SimpleSection toSimpleSection() {
        return new SimpleSection(upStationId, downStationId, distance);
    }
}
