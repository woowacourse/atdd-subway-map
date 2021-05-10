package wooteco.subway.line;

import java.util.Objects;
import wooteco.subway.exception.NotInputDataException;
import wooteco.subway.line.dto.LineRequest;

public class Line {

    private Long id;
    private String name;
    private String color;

    public Line() {
    }

    public Line(LineRequest lineRequest) {
        this(lineRequest.getName(), lineRequest.getColor());
    }

    public Line(Long id, LineRequest lineRequest) {
        this(id, lineRequest.getName(), lineRequest.getColor());
    }

    public Line(String name, String color) {
        this(0L, name, color);
    }

    public Line(Long id, String name, String color) {
        validate(name, color);
        this.id = id;
        this.name = name.trim();
        this.color = color.trim();
    }

    private void validate(String name, String color) {
        if (Objects.isNull(name) || Objects.isNull(color)
            || name.trim().length() == 0 || color.trim().length() == 0) {
            throw new NotInputDataException();
        }
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
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name)
            && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
