package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class LineRequest {

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;
    @NotBlank(message = "색깔을 입력해주세요.")
    private String color;
    private Long upStationId;
    private Long downStationId;
    @Positive(message = "구간의 길이는 양수여야 합니다.")
    private int distance;

    public LineRequest() {
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
