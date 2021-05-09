package wooteco.subway.line;

import java.util.List;

import wooteco.subway.exception.IllegalInputException;
import wooteco.subway.station.Station;

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
    }

    public Line(Long id, String name, String color) {
        this(name, color);
        this.id = id;
    }

    public Line(long id, Line line) {
        this(line.getName(), line.getColor());
        this.id = id;
    }

    public Line(Line line, List<Station> stations) {
        this(line.getId(), line.getName(), line.getColor());
        this.stations = stations;
    }

    public Line(LineRequest lineRequest) {
        this(lineRequest.getName(), lineRequest.getColor());
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
}
