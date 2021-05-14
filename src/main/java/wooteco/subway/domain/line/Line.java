package wooteco.subway.domain.line;

import java.util.List;
import java.util.Objects;

import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.illegal.IllegalInputException;
import wooteco.subway.dto.line.LineRequest;

public class Line {

    private final String name;
    private final String color;
    private Long id;
    private StationsInLine stationsInLine;

    public Line(String name, String color) {
        validateName(name);
        validateColor(name);
        this.name = name;
        this.color = color;
        this.stationsInLine = new StationsInLine();
    }

    public Line(Long id, String name, String color) {
        this(name, color);
        this.id = id;
    }

    public Line(Line line, StationsInLine stationsInLine) {
        this(line.getId(), line.getName(), line.getColor());
        this.stationsInLine = stationsInLine;
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
        return stationsInLine.getStations();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Line line = (Line)o;
        return Objects.equals(name, line.name) && Objects.equals(color, line.color) && Objects
            .equals(id, line.id) && Objects.equals(stationsInLine, line.stationsInLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, id, stationsInLine);
    }
}
