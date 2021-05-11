package wooteco.subway.line;

import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.station.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Line {

    private Long id;
    private String name;
    private String color;
    private Sections sections;

    public Line() {
    }

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(long id, String name, String color) {
        this(id, name, color, null);
    }

    public boolean insertSectionAtEdge(Section section) {
        return sections.insertSectionAtEdge(section);
    }

    public Map<Section, Section> insertSectionInBetween(Section section) {
        return sections.insertSectionInBetween(section);
    }

    public boolean checkSectionAtEdge(Station station) {
        return sections.checkSectionAtEdge(station);
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
        this.sections = sections;
    }
}
