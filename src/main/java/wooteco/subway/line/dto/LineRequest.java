package wooteco.subway.line.dto;

import wooteco.subway.line.domain.Line;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class LineRequest {
    @NotBlank(message = "이름은 공백일 수 없습니다.")
    private String name;
    @NotBlank(message = "색상은 공백일 수 없습니다.")
    private String color;
    @Positive(message = "부적절한 station_id입니다.")
    private Long firstStationId;
    @Positive(message = "부적절한 station_id입니다.")
    private Long lastStationId;
    @Positive(message = "구간 길이는 음수 또는 0일 수 없습니다.")
    private int distance;

    public LineRequest() {
    }

    public LineRequest(final String name, final String color, final Long firstStationId, final Long lastStationId, final int distance) {
        this.name = name;
        this.color = color;
        this.firstStationId = firstStationId;
        this.lastStationId = lastStationId;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getFirstStationId() {
        return firstStationId;
    }

    public Long getLastStationId() {
        return lastStationId;
    }

    public int getDistance() {
        return distance;
    }

    public Line toLine() {
        return new Line(null, name, color, firstStationId, lastStationId);
    }

    public Line toLine(final Long id) {
        return new Line(id, name, color, firstStationId, lastStationId);
    }
}
