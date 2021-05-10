package wooteco.subway.line.domain;

import wooteco.subway.exception.EmptyInputException;
import wooteco.subway.exception.NullInputException;
import wooteco.subway.exception.line.LineSuffixException;
import wooteco.subway.section.Section;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Line {
    private static final String SUFFIX = "선";

    private Long id;
    private String name;
    private String color;

    public Line() {
    }

    public Line(Long id, String name, String color) { //TODO 삭제
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        validateNotNull(name, color);
        validateNotEmpty(name, color);
        validateSuffix(name);
        this.name = name;
        this.color = color;
    }

    private void validateNotNull(String name, String color) {
        if (name == null || color == null) {
            throw new NullInputException();
        }
    }

    private void validateNotEmpty(String name, String color) {
        if ("".equals(name) || "".equals(color)) {
            throw new EmptyInputException();
        }
    }

    private void validateSuffix(String name) {
        if (isNotEndsWithLine(name)) {
            throw new LineSuffixException();
        }
    }

    private boolean isNotEndsWithLine(String name) {
        return !name.endsWith(SUFFIX);
    }

    public boolean isSameColor(Line line) {
        return this.color.equals(line.color);
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
        return Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
