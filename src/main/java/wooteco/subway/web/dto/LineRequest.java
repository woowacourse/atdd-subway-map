package wooteco.subway.web.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;

public class LineRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String color;
    @Min(1)
    @NotNull
    private Long upStationId;
    @Min(1)
    @NotNull
    private Long downStationId;
    @Min(1)
    @NotNull
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId,
            int distance) {
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

    public Line toEntity() {
        return new Line(name, color);
    }

    public Section toStationEntity() {
        return new Section(null, upStationId, downStationId);
    }
}
