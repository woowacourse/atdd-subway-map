package wooteco.subway.domain;

import wooteco.subway.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private final long id;
    private final String name;
    private final String color;
    private final List<Station> stations;

    public Line(String name, String color) {
        this.id = 1L;
        this.name = name;
        this.color = color;
        this.stations = new ArrayList<>();
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
