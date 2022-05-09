package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Objects;

public class Line {

    private Long id;
    private final String name;
    private final String color;
    private final ArrayList<Station> stations;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.stations = null;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = null;
    }

    public Line(Long id, String name, String color, ArrayList<Station> stations) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
