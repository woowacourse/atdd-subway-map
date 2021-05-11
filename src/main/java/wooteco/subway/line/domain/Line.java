package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Line {
    private final Long id;
    private final String color;
    private final String name;
    private List<Station> stations;

    public Line(final String color, final String name) {
        this(null, color, name, new ArrayList<>());
    }

    public Line(final Long id, final String color, final String name) {
        this(id, color, name, new ArrayList<>());
    }

    public Line(final Long id, final String color, final String name, final List<Station> stations) {
        this.id = id;
        this.color = color;
        this.name = name;
        this.stations = stations;
    }

    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void addStations(final List<Station> stations) {
        this.stations.addAll(stations);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
