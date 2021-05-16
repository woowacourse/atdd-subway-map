package wooteco.subway.line.dto;

import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.station.domain.Station;

import javax.validation.constraints.Pattern;

public class LineRequest {
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]*선$", message = "지하철 노선 이름이 잘못되었습니다.")
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this(name, color, null, null, 0);
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

    public Line toLineEntity() {
        return new Line(null, name, color);
    }

    public Section toLinesEntity() {
        return new Section(
                new Line(name, color),
                new Station(upStationId),
                new Station(downStationId),
                distance);
    }
}
