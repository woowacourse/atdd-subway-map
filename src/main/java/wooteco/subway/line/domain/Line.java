package wooteco.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.station.domain.Station;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private final List<Station> stations;

    public Line(final LineRequest lineRequest) {
        this(null, lineRequest);
    }

    public Line(final String name, final String color) {
        this(null, name, color);
    }

    public Line(final Long id, final LineRequest lineRequest) {
        this(id, lineRequest.getName(), lineRequest.getColor(), new ArrayList<>());
    }

    public Line(final Long id, final String name, final String color) {
        this(id, name, color, Collections.emptyList());
    }

    public Line(final Long id, final String name, final String color, final List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public void addStations(final List<Station> stations) {
        this.stations.addAll(stations);
    }

    public boolean isId(Long id) {
        return this.id.equals(id);
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
        return Collections.unmodifiableList(stations);
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
