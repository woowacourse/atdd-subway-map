package wooteco.subway.line.domain;

import wooteco.subway.common.exception.AlreadyExistsException;
import wooteco.subway.common.exception.NotFoundException;
import wooteco.subway.line.state.State;
import wooteco.subway.line.state.StateFactory;
import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.Objects;

public class Line {
    private final Long id;
    private LineName name;
    private String color;
    private final State state;

    public Line(final Long id) {
        this(id, LineName.emptyName(), null);
    }

    public Line(final String name, final String color) {
        this(null, new LineName(name), color);
    }

    public Line(final Long id, final String name, final String color) {
        this(id, new LineName(name), color);
    }

    public Line(final Long id, final LineName name, final String color) {
        this(id, name, color, StateFactory.create(new Sections()));
    }

    public Line(final Long id, final String name, final String color, final List<Section> sections) {
        this(id, new LineName(name), color, StateFactory.create(new Sections(sections)));
    }

    public Line(final Long id, final LineName name, final String color, final State state) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.state = state;
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name.name();
    }

    public String color() {
        return color;
    }

    public Sections sections() {
        return state.sections();
    }

    public List<Section> sortedSections() {
        return state.sortedSections();
    }

    public void addSection(final Section targetSection) {
        if (Objects.isNull(targetSection)) {
            return;
        }

        if (state.sections().sections().size() < 1) {
            this.state.sections().addSection(targetSection);
            targetSection.changeLine(this);
            return;
        }

        validateDuplicationStation(targetSection.upStation(), targetSection.downStation());
        validateContain(targetSection.upStation(), targetSection.downStation());

        state.addSection(targetSection);
        targetSection.changeLine(this);
    }

    public void deleteStation(final Station station) {
        state.deleteStation(station);
    }

    public boolean sameName(final String name) {
        return this.name.sameName(name);
    }

    public boolean sameId(final Long id) {
        return this.id.equals(id);
    }

    public void changeName(final String name) {
        this.name = this.name.changeName(name);
    }

    public void changeColor(String color) {
        this.color = color;
    }

    private void validateDuplicationStation(final Station upStation, final Station downStation) {
        if (state.existSection(upStation, downStation)) {
            throw new AlreadyExistsException("이미 등록되어 있는 구간임!");
        }
    }

    private void validateContain(final Station upStation, final Station downStation) {
        if (state.noContainStation(upStation, downStation)) {
            throw new NotFoundException("상행, 하행역 둘다 노선에 등록되어 있지 않음!!");
        }
    }
}
