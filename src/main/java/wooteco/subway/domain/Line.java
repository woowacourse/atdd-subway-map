package wooteco.subway.domain;

import java.util.List;

public class Line {

    private Long id;
    private String name;
    private String color;
    private Sections sections;

    private Line() {
    }

    public Line(Long id, String name, String color) {
        validateNameNotEmpty(name);
        validateColorNotEmpty(color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        validateNameNotEmpty(name);
        validateColorNotEmpty(color);
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Sections sections) {
        validateNameNotEmpty(name);
        validateColorNotEmpty(color);
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(Long id, String name, String color, Sections sections) {
        validateNameNotEmpty(name);
        validateColorNotEmpty(color);
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public void add(Section section) {
        sections.add(section);
    }

    public void delete(Station station) {
        sections.delete(station);
    }

    private void validateNameNotEmpty(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("이름은 비워둘 수 없습니다.");
        }
    }

    private void validateColorNotEmpty(String color) {
        if (color.isBlank()) {
            throw new IllegalArgumentException("색상은 비워둘 수 없습니다.");
        }
    }

    public List<Section> getSections() {
        return sections.getSections();
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
}
