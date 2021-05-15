package wooteco.subway.line;

import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.station.Station;

import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Sections sections;

    public Line(long id) {
        this(id, null, null, new Sections());
    }

    public Line(String name, String color) {
        this(null, name, color, new Sections());
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, new Sections());
    }

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public void addSection(Section section) {
        this.sections.addSection(section);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void updateSections(Sections sections) {
        this.sections = sections;
    }

    public void deleteStationInSection(Station station) {
        sections.delete(station);
    }

    public List<Station> stations() {
        return sections.getStations();
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
}
