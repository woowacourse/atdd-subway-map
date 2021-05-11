package wooteco.subway.line;

import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.station.Station;

import java.util.ArrayList;
import java.util.List;

public class Line {

    private Long id;
    private String name;
    private String color;
    //Validation 필요
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

    public void insertSection(Section section) {
        sections.insertSection(section);
    }

    public void removeSection(Station station) {
        sections.removeSection(station);
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
