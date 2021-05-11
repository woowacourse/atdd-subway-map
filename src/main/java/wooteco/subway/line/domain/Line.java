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

    public Line(final String name, final String color) {
        this(null, new LineName(name), color);
    }

    public Line(final Long id, final String name, final String color) {
        this(id, new LineName(name), color);
    }

    public Line(final Long id, final Name name, final String color) {
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

    public void addSection(final Section targetSection) {
        validateDuplicationStation(targetSection.upStation(), targetSection.downStation());
        validateContain(targetSection.upStation(), targetSection.downStation());

        state.addSection(this, targetSection);
    }

    public void deleteStation(final Station station) {
        state.deleteStation(station);
    }

    private void validateDuplicationStation(final Station upStation, final Station downStation) {
        if (state.containStation(upStation) && state.containStation(downStation)) {
            throw new IllegalStateException("이미 등록되어 있는 구간임!");
        }
    }

    private void validateContain(final Station upStation, final Station downStation) {
        if (!state.containStation(upStation) && !state.containStation(downStation)) {
            throw new IllegalStateException("노선에 등록되어 있지 않은 상행, 하행역임!!");
        }
    }
}
