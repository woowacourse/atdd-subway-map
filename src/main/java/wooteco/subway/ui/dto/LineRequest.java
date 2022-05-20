package wooteco.subway.ui.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public class LineRequest {

    @NotBlank
    @Length(min = 1, max = 255)
    private String name;
    @NotBlank
    @Length(min = 1, max = 20)
    private String color;
    @Positive
    @NotNull
    private Long upStationId;
    @Positive
    @NotNull
    private Long downStationId;
    @Positive
    @Min(value = 1)
    private int distance;

    private LineRequest() {
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public LineRequest(String name, String color) {
        this(name, color, null, null, 0);
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
