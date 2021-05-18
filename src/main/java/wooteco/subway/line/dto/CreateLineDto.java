package wooteco.subway.line.dto;

import javax.validation.constraints.NotNull;
import wooteco.subway.line.domain.Line;

public class CreateLineDto {

    @NotNull
    private String name;
    @NotNull
    private String color;
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private int distance;

    public CreateLineDto(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static CreateLineDto from(final LineRequest dto) {
        return new CreateLineDto(dto.getName(), dto.getColor(), dto.getUpStationId(), dto.getDownStationId(), dto.getDistance());
    }

    public Line toLineEntity() {
        return new Line(name, color);
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

