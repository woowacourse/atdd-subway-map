package wooteco.subway.line;


import java.util.ArrayList;
import java.util.List;
import wooteco.subway.station.Station;

public class Line {
    private Long id;
    private String name;
    private String color;
    private List<Station> stations;

    public Line(String name, String color) {
        this(null, name, color, new ArrayList<>());
    }

    public Line(String name, String color, List<Station> stations) {
        this(null, name, color, stations);
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, new ArrayList<>());
    }

    public Line(Long id, String name, String color, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public boolean isSameName(final String name) {
        return this.name.equals(name);
    }

    public boolean isSameId(final Long id) {
        return this.id == id;
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
