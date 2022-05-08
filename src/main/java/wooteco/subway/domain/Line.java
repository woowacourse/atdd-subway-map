package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private Long id;
    private Long upStationId;
    private Long downStationId;
    private String name;
    private String color;
    private Long distance;
    List<Station> stations = new ArrayList<>();

    public Line(Long id, Long upStationId, Long downStationId, String name, String color, Long distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public Line(Long upStationId, Long downStationId, String name, String color, Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public Long getId() {
        return id;
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

    public Long getDistance() {
        return distance;
    }

    public List<Station> getStations() {
        return stations;
    }
}
