package wooteco.subway.domain;

import java.util.List;

public class Line {

    private final Long id;
    private final String name;
    private final LineColor color;
    private final Sections sections;

    public Line(final Long id, final String name, final String color, final Sections sections) {
        this.id = id;
        this.name = name;
        this.color = new LineColor(color);
        this.sections = sections;
    }

    public static Line createWithoutId(final String name, final String color, final Sections sections) {
        return new Line(null, name, color, sections);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color.getValue();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }
}
