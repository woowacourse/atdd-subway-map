package wooteco.subway.line.domain;

import wooteco.subway.line.state.State;
import wooteco.subway.line.state.StateFactory;
import wooteco.subway.name.domain.LineName;
import wooteco.subway.name.domain.Name;
import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private final Long id;
    private final Name name;
    private final String color;
    private final State state = StateFactory.initialize(new Sections());

    public Line(String name, String color) {
        this(null, new LineName(name), color);
    }

    public Line(Long id, String name, String color) {
        this(id, new LineName(name), color);
    }

    public Line(Long id, Name name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public String nameAsString() {
        return name.name();
    }

    public String getColor() {
        return color;
    }

    public Sections getSections() {
        return state.sections();
    }

    public void addSection(Section targetSection) {
        state.addSection(this, targetSection);
    }

    public void deleteStation(Station station) {
        state.deleteStation(station);
    }
}
