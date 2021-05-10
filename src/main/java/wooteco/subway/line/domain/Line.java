package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Sections sections;

    public Line(String name, String color) {
        this(0L, name, color, new ArrayList<>());
    }

    public Line(String name, String color, List<Section> sections) {
        this(0L, name, color, sections);
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, new ArrayList<>());
    }

    public Line(Long id, String name, String color, List<Section> sections) {
        this(id, name, color, new Sections(sections));
    }

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String color() {
        return color;
    }

    public boolean sameId(final Long id) {
        return this.id.equals(id);
    }

    public boolean sameName(final String name) {
        return this.name.equals(name);
    }

    public void changeName(final String name) {
        this.name = name;
    }

    public void changeColor(String color) {
        this.color = color;
    }

    public void initSections(List<Section> sections) {
        this.sections = new Sections(sections);
    }

    public void addSection(Section section) {
        //  line의 section에 upstationId와 downStationId 둘다 존재하는지 - 노선의 구간에 이미 등록되어있음
        sections.add(section);
    }

    public List<Section> sections() {
        return sections.sections();
    }

    public List<Station> stations() {
        return sections.sortedStations();
    }
}
