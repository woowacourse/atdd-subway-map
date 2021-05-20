package wooteco.subway.line.domain;

import wooteco.subway.common.exception.NotFoundException;
import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.Objects;

public class Line {
    private final Long id;
    private LineName name;
    private String color;
    private final Sections sections;

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
        this(id, name, color, new Sections());
    }

    public Line(final Long id, final String name, final String color, final List<Section> sections) {
        this(id, new LineName(name), color, new Sections(sections));
    }

    public Line(final Long id, final LineName name, final String color, final Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
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

    public List<Section> sortedSections() {
        return sections.sortedSections();
    }

    public void addSection(final Section targetSection) {
        if (Objects.isNull(targetSection)) {
            throw new NotFoundException("타겟을 찾을 수 없음!");
        }
        this.sections.addSection(targetSection);
    }

    public void deleteStation(final Station station) {
        sections.deleteStation(station);
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

    public void changeColor(final String color) {
        this.color = color;
    }
}
