package wooteco.subway.line.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import wooteco.subway.line.service.dto.LineCreateDto;

public class LineRequest {

    @Pattern(regexp = "^[가-힣a-zA-Z0-9]*선$", message = "지하철 노선 이름은 'XX선' 으로 끝나야합니다.")
    @Size(min=3, max=12, message = "지하철 노선 이름은 최소 3글자, 최대 12글자로 이루어져야합니다.")
    private String name;
    @NotBlank
    private String color;
    @Positive
    private Long upStationId;
    @Positive
    private Long downStationId;
    @Positive
    private int distance;

    public LineRequest() {
    }

    public LineRequest(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(final String name, final String color, final Long upStationId, final Long downStationId, final int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public LineCreateDto toLineCreateDto() {
        return new LineCreateDto(
                name,
                color,
                upStationId,
                downStationId,
                distance
        );
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
