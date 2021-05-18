package wooteco.subway.line;

import wooteco.subway.exception.line.LineLengthException;
import wooteco.subway.exception.line.LineSuffixException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Objects;

public class Line {
    private static final String SUFFIX = "선";

    @NotNull
    @Positive
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String color;

    public Line() {
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        validateSuffix(name);
        validateLength(name);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateSuffix(String name) {
        if (isNotEndsWithLine(name)) {
            throw new LineSuffixException();
        }
    }

    private boolean isNotEndsWithLine(String name) {
        return !name.endsWith(SUFFIX);
    }

    private void validateLength(String name) {
        if (name.length() < 2) {
            throw new LineLengthException();
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
