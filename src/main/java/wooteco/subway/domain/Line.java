package wooteco.subway.domain;

import java.util.Collections;
import java.util.Objects;

public class Line {

    private static final String NULL_PREVENT_MESSAGE = "[ERROR] 필수 입력 사항을 입력해주세요. cause : ";

    private final Long id;
    private final String name;
    private final String color;
    private final Sections sections;

    public Line(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = Objects.requireNonNull(name, NULL_PREVENT_MESSAGE + "name");
        this.color = Objects.requireNonNull(color, NULL_PREVENT_MESSAGE + "color");
        this.sections = Objects.requireNonNull(sections, NULL_PREVENT_MESSAGE + "sections");
    }

    public Line(Long id, String name, String color) {
        this(id, name, color, new Sections(Collections.emptyList()));
    }

    public Line(String name, String color) {
        this(null, name, color, new Sections(Collections.emptyList()));
    }

    public boolean isSameName(String name) {
        return name.equals(this.name);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sections=" + sections +
                '}';
    }
}
