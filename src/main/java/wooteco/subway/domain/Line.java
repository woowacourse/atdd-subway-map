package wooteco.subway.domain;

import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Sections sections;

    private Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this(name, color);
        this.id = id;
    }

    public Line(String name, String color, Section section) {
        this(name, color);
        this.sections = new Sections(List.of(section));
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void deleteSections(Station station) {
        sections.delete(station);
    }

    public boolean isSameName(Line line) {
        return name.equals(line.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Sections getSections() {
        return sections;
    }

    public List<Section> getAllSections() {
        return sections.getSections();
    }
}
