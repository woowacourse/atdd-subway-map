package wooteco.subway.dao.entity;

import java.util.Objects;

public class LineEntity {

    private Long id;
    private String name;
    private String color;

    public LineEntity(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineEntity(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineEntity lineEntity = (LineEntity) o;
        return getName().equals(lineEntity.getName()) && getColor().equals(lineEntity.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getColor());
    }
}
