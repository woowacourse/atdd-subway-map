package wooteco.subway.line;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

public class LineRequest {
    @NotBlank(message = "name은 공백이 될 수 없습니다")
    @Pattern(regexp = ".*선$", message = "노선 이름은 ~선으로 끝나야 합니다.")
    private String name;
    @NotBlank(message = "color는 공백이 될 수 없습니다")
    private String color;
    @NotNull(message = "upStationId는 null이 될 수 없습니다")
    private Long upStationId;
    @NotNull(message = "downStationId는 null이 될 수 없습니다")
    private Long downStationId;
    @NotNull(message = "distance는 null이 될 수 없습니다")
    @Positive(message = "distance는 0보다 커야합니다")
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
