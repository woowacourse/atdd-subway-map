package wooteco.subway.line;

import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.station.Station;

import java.util.List;
import java.util.Map;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private Sections sections;

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(long id, String name, String color) {
        this(id, name, color, new Sections());
    }

    public void insertSectionAtEdge(Section section) {
        sections.insertSectionAtEdge(section);
    }

    public Map<Section, Section> insertSectionInBetween(Section section) {
        return sections.insertSectionInBetween(section);
    }

    public boolean checkSectionAtEdge(Station station) {
        return sections.checkSectionAtEdge(station);
    }

    public boolean checkSectionAtEdge(Section section) {
        return sections.checkSectionAtEdge(section);
    }

    public Section removeSectionAtEdge(Station station) {
        return sections.removeSectionAtEdge(station);
    }

    public Map<Section, Map<Section, Section>> removeSectionInBetween(Station station) {
        return sections.removeSectionInBetween(station);
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
        return sections.lineUpStations();
    }

    public void setSections(Sections sections) {
        validateSections(sections);
        this.sections = sections;
    }

    private void validateSections(Sections sections) {
        if (!sections.checkSameLineId(id)) {
            throw new IllegalStateException("노선에 등록될 수 없는 구간입니다.");
        }
    }
}
