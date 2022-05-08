package wooteco.subway.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import wooteco.subway.domain.Line;

public class LineSaveRequest {

    @NotBlank(message = "line 이름은 공백 혹은 null이 들어올 수 없습니다.")
    private String name;

    @NotBlank(message = "line 색상은 공백 혹은 null이 들어올 수 없습니다.")
    private String color;

    private long upStationId;

    private long downStationId;

    @Positive(message = "상행-하행 노선 길이는 1 이상의 값만 들어올 수 있습니다.")
    private int distance;

    private LineSaveRequest() {
    }

    public LineSaveRequest(final String name, final String color, final long upStationId, final long downStationId,
                           final int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Line toLine() {
        return new Line(name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
