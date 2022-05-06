package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private List<Station> stations;

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
        stations = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getId() {
        return id;
    }

    public List<Station> getStations() {
        return stations;
    }
}
