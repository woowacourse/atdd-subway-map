package wooteco.subway.domain;

import java.util.List;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private final Sections sections;

    public Line(final String name, final String color) {
        this(null, name, color, new Sections());
    }

    public Line(final Long id, final String name, final String color) {
        this(id, name, color, new Sections());
    }

    public Line(final Long id, final String name, final String color, final Sections sections) {
        validateName(name);
        validateColor(color);
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("노선 이름은 공백일 수 없습니다.");
        }
    }

    private void validateColor(final String color) {
        if (color.isBlank()) {
            throw new IllegalArgumentException("색상이 공백일 수 없습니다.");
        }
    }

    public List<Station> getSortedStations() {
        return sections.toSortedStations();
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
