package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private static final String ERROR_NULL = "[ERROR] 이름에 빈칸 입력은 허용하지 않습니다.";

    private Long id;
    private Name name;
    private String color;


    public Line(String name,
                String color) {
        this(null, name, color);
    }

    public Line(Long id,
                final String name,
                final String color) {
        Objects.requireNonNull(name, ERROR_NULL);
        Objects.requireNonNull(color, ERROR_NULL);
        this.id = id;
        this.name = new Name(name);
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Line line = (Line) o;

        if (getId() != null ? !getId().equals(line.getId()) : line.getId() != null) {
            return false;
        }
        if (getName() != null ? !getName().equals(line.getName()) : line.getName() != null) {
            return false;
        }
        return getColor() != null ? getColor().equals(line.getColor()) : line.getColor() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getColor() != null ? getColor().hashCode() : 0);
        return result;
    }
}
