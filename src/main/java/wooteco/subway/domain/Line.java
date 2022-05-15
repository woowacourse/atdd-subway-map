package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Objects;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private final ArrayList<Station> stations;

    public Line(Long id, String name, String color, ArrayList<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, null);
    }

    public Line(String name, String color) {
        this(null, name, color, null);
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

    public ArrayList<Station> getStations() {
        return stations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
