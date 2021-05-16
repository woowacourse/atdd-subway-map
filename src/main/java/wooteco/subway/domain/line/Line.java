package wooteco.subway.domain.line;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.illegal.IllegalInputException;
import wooteco.subway.dto.line.LineRequest;

public class Line {

    private final String name;
    private final String color;
    private Long id;
    private List<Station> stations;

    public Line(String name, String color) {
        validateName(name);
        validateColor(name);
        this.name = name;
        this.color = color;
        this.stations = new ArrayList<>();
    }

    public Line(Long id, String name, String color) {
        this(name, color);
        this.id = id;
    }

    public Line(Line line, List<Station> stations) {
        this(line.getId(), line.getName(), line.getColor());
        this.stations = stations;
    }

    public Line(LineRequest lineRequest) {
        this(lineRequest.getName(), lineRequest.getColor());
    }

    public static Line of(long id, Line line) {
        return new Line(id, line.getName(), line.getColor());
    }

    private void validateColor(String color) {
        if (color == null || color.isEmpty()) {
            throw new IllegalInputException();
        }
    }

    private void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalInputException();
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Line line = (Line)o;
        return Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects
            .equals(id, line.id) && Objects.equals(stations, line.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, id, stations);
    }
}
