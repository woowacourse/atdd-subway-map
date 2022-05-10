package wooteco.subway.domain;

import java.util.List;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private final Stations stations;

    public Line(Long id, String name, String color, Stations stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
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
        return stations.getStations();
    }
}
