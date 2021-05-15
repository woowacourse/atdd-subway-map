package wooteco.subway.domain.line;

import java.util.List;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;

public class Line {

    private long id;
    private String name;
    private String color;
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSections(Sections sections) {
        this.sections = sections;
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public void addSection(Section section) {
        sections.addSection(section);
    }

    public void removeSectionsBy(Station station) {
        sections.removeSectionsBy(station);
    }

    public Section findAffectedSectionByDeleteStation(Station station) {
        return sections.findAffectedSectionByDeleteStation(station);
    }

    public List<Section> findAffectedSectionByAddSection(Section section) {
        return sections.findAffectedSectionByAddSection(section);
    }
}

