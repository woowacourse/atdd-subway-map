package wooteco.subway.line;

import java.util.Objects;

public class Line {

    private LineId id;
    private String name;
    private String color;

    public Line() {
    }

    public Line(final String name, final String color) {
        this.id = new LineId(null);
        this.name = name;
        this.color = color;
    }

    public Line(final Long id, final String name, final String color) {
        this.id = new LineId(id);
        this.name = name;
        this.color = color;
    }

    public Line(final String name) {
        this.name = name;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeColor(String color) {
        this.color = color;
    }

    public Long getId() {
        if (id == null) {
            return null;
        }
        return id.getId();
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
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
