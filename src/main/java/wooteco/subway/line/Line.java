package wooteco.subway.line;

import java.util.Objects;
import wooteco.subway.StringInput;

public class Line {

    private final Long id;
    private final StringInput name;
    private final StringInput color;

    public Line(String name, String color) {
        this(0L, name, color);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = new StringInput(name);
        this.color = new StringInput(color);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getItem();
    }

    public String getColor() {
        return color.getItem();
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
        return Objects.equals(id, line.id) && Objects.equals(name, line.name)
            && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
