package wooteco.subway.line;

import wooteco.subway.station.Station;

import java.util.List;
import java.util.Objects;

public class Line {
    private final Long id;
    private final String name;
    private final String color;
    private final List<Station> stations;

    public Line(final Long id, final String name, final String color) {
        this(id, name, color, null);
    }

    public Line(final Long id, final String name, final String color, final List<Station> stations) {
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
        return id.equals(line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
