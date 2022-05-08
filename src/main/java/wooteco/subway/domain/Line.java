package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Long distance;
    List<Station> stations = new ArrayList<>();

    public Line(Long id, String name, String color, Long distance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public Line(String name, String color, Long distance) {
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

    public Long getDistance() {
        return distance;
    }

    public List<Station> getStations() {
        return stations;
    }
}
