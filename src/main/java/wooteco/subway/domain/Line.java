package wooteco.subway.domain;

import java.util.List;
import wooteco.subway.exception.IllegalLineColorException;
import wooteco.subway.exception.IllegalLineNameException;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private final List<Station> stations;

    public Line(final Long id, final String name, final String color, final List<Station> stations) {
        validateName(name);
        validateColor(color);
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new IllegalLineNameException();
        }
    }

    private void validateColor(final String color) {
        if (color.isBlank()) {
            throw new IllegalLineColorException();
        }
    }

    public Line(final String name, final String color, final List<Station> stations) {
        this(null, name, color, stations);
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
