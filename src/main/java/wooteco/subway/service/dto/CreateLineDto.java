package wooteco.subway.service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.domain.Line;

public class CreateLineDto {

    @NotEmpty
    private final String name;
    @NotBlank
    private final String color;
    @NotNull
    private final Long upStationId;
    @NotNull
    private final Long downStationId;
    @NotNull
    private final int distance;

    public CreateLineDto(String name, String color, Long upStationId, Long downStationId,
        int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static CreateLineDto from(LineRequest dto) {
        return new CreateLineDto(dto.getName(), dto.getColor(), dto.getUpStationId(),
            dto.getDownStationId(), dto.getDistance());
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
