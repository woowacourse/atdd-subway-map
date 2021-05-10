package wooteco.subway.line.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class LineRequest {
    @NotBlank
    @Pattern(regexp = "^[가-힣|0-9]*선$")
    private String name;
    @NotBlank
    private String color;
    @NotNull(groups = LineInfo.save.class)
    private Long upStationId;
    @NotNull(groups = LineInfo.save.class)
    private Long downStationId;
    @NotNull(groups = LineInfo.save.class)
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
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
}
