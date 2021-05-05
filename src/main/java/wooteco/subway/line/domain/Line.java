package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private List<Station> stations;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.stations = new ArrayList<>();
    }

    public Line(Long id, String name, String color, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public Line() {
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

    public List<Station> getStations() {
        return stations;
    }

    public boolean equalName(Line line) {
        return this.name.equals(line.name);
    }

    public boolean equalId(Long id) {
        return this.id.equals(id);
    }
}
