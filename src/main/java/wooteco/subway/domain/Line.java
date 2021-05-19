package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;

import wooteco.subway.exception.EntityNotFoundException;

public class Line {
    private final Long id;
    private final String name;
    private final String color;
    private final Sections sections;

    public Line(Long id, Line line) {
        this(id, line.name, line.color, line.sections);
    }

    public Line(Line line, Sections sections) {
        this(line.id, line.name, line.color, sections);
    }

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.color = Objects.requireNonNull(color);
        this.sections = Objects.requireNonNull(sections);
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

    public Sections getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return sections.getSortedStations();
    }

    public boolean isNameEquals(String name) {
        return this.name.equals(name);
    }

    public Line insertSection(Section section) {
        return new Line(this, sections.insert(section));
    }

    public boolean shouldInsertAtSide(Section section) {
        return sections.shouldInsertAtTop(section) || sections.shouldInsertAtBottom(section);
    }

    public boolean shouldInsertAtUpStationOfMiddle(final Section section) {
        return sections.isStationInUpStations(section.getUpStation());
    }

    public Section findSectionByUpStation(Station upStation) {
        return sections.findByUpStation(upStation)
                       .orElseThrow(() -> new EntityNotFoundException("해당 역과 일치하는 구간이 존재하지 않습니다."));
    }

    public boolean isTopStation(Station station) {
        return sections.isTopStation(station);
    }

    public boolean isBottomStation(Station station) {
        return sections.isBottomStation(station);
    }

    public Sections deleteStation(Station station) {
        return this.sections.deleteStation(station);
    }
}
