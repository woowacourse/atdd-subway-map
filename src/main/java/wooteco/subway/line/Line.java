package wooteco.subway.line;

import wooteco.subway.name.LineName;
import wooteco.subway.name.Name;

public class Line {
    private Long id;
    private Name name;
    private String color;

    public Line(final String name, final String color) {
        this(new LineName(name), color);
    }

    public Line(final Name name, final String color) {
        this(0L, name, color);
    }

    public Line(final Long id, final String name, final String color) {
        this(id, new LineName(name), color);
    }

    public Line(final Long id, final Name name, final String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }
}
