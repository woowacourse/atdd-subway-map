package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private Long id;
    private String name;
    private String color;
    private Section section;

    public Line(final String name, final String color, final Section section) {
        this(null, name, color, section);
    }

    public Line(final Long id, final String name, final String color, final Section section) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.section = section;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getId() {
        return id;
    }

    public Section getSection() {
        return section;
    }
}
