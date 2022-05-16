package wooteco.subway.domain;

import wooteco.subway.exception.IllegalLineColorException;
import wooteco.subway.exception.IllegalLineNameException;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private final SectionsOnTheLine sectionsOnTheLine;

    public Line(final Long id, final String name, final String color, final SectionsOnTheLine sectionsOnTheLine) {
        validateName(name);
        validateColor(color);
        this.id = id;
        this.name = name;
        this.color = color;
        this.sectionsOnTheLine = sectionsOnTheLine;
    }

    private void validateName(final String name) {
        if (name.equals(null) || name.isBlank()) {
            throw new IllegalLineNameException();
        }
    }

    private void validateColor(final String color) {
        if (color.equals(null) || color.isBlank()) {
            throw new IllegalLineColorException();
        }
    }

    public static Line ofNullId(final String name, final String color, final SectionsOnTheLine sectionsOnTheLine) {
        return new Line(null, name, color, sectionsOnTheLine);
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

    public SectionsOnTheLine getSectionsOnTheLine() {
        return sectionsOnTheLine;
    }
}
