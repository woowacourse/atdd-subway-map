package wooteco.subway.line.domain;

import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.Arrays;
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

    public Line(Long id, String name, String color, Section section) {
        this(id, name, color, Arrays.asList(section));
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

    public Line addedSectionLine(Section section) {
        Sections newSections = this.sections.addedSections(section);
        return new Line(id, name, color, newSections);
    }

    public List<Station> stations() {
        return sections.sortedStations();
    }

    public Section affectedSection(Line originLine) {
        return sections.affectedSection(originLine.sections);
    }
}
