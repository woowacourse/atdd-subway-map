package wooteco.subway.line.domain;

import wooteco.subway.exception.illegalexception.IllegalLineArgumentException;
import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private List<Station> stations;

    public Line() {
    }

    public Line(String name, String color) {
        this(0L, name, color, new ArrayList<>());
    }

    public Line(Long id, String name, String color, List<Station> stations) {
        validatedNameLength(name);
        validatedColorLength(color);
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
        return stations;
    }

    public boolean equalName(String name) {
        return this.name.equals(name);
    }

    public boolean equalId(Long id) {
        return this.id.equals(id);
    }

    private static void validatedNameLength(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalLineArgumentException("유효한 이름이 아닙니다.");
        }
    }

    private static void validatedColorLength(String color) {
        if (color == null || color.length() == 0) {
            throw new IllegalLineArgumentException("유효한 색깔이 아닙니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;

        Line line = (Line) o;

        if (!getId().equals(line.getId())) return false;
        if (!getName().equals(line.getName())) return false;
        if (!getColor().equals(line.getColor())) return false;
        return getStations().equals(line.getStations());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getColor().hashCode();
        result = 31 * result + getStations().hashCode();
        return result;
    }
}
