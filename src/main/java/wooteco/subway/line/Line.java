package wooteco.subway.line;

import wooteco.subway.station.Station;

import java.util.Collections;
import java.util.List;

public class Line {
    private static final Long NOT_EXIST_ID = -1L;

    private Long id;
    private String name;
    private String color;
    private List<Station> stations;

    public Line(String name, String color) {
        this(NOT_EXIST_ID, name, color, Collections.emptyList());
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, Collections.emptyList());
    }

    public Line(Long id, String name, String color, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
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
}
