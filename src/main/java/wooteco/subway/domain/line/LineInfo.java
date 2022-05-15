package wooteco.subway.domain.line;

import java.util.Objects;
import wooteco.subway.domain.section.RegisteredSection;

public class LineInfo {

    private final Long id;
    private final String name;
    private final String color;

    public LineInfo(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineInfo of(RegisteredSection registeredSection) {
        Long id = registeredSection.getLineId();
        String name = registeredSection.getLineName();
        String color = registeredSection.getLineColor();
        return new LineInfo(id, name, color);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineInfo line = (LineInfo) o;
        return Objects.equals(id, line.id)
                && Objects.equals(name, line.name)
                && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
