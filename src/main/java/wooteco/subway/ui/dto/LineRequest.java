package wooteco.subway.ui.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LineRequest {

    @NotBlank(message = "name을 입력해주세요.")
    private String name;
    @NotBlank(message = "color를 입력해주세요.")
    private String color;
    @NotNull(message = "upStationId를 입력해주세요.")
    private Long upStationId;
    @NotNull(message = "downStationId를 입력해주세요.")
    private Long downStationId;
    @NotNull(message = "distance를 입력해주세요.")
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
}
