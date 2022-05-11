package wooteco.subway.domain;

import java.util.List;
import wooteco.subway.exception.LineColorLengthException;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private final Sections sections;

    public Line(final Long id, final String name, final String color, final Sections sections) {
        validateColor(color);
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    private void validateColor(final String color) {
        if (color.length() > 20) {
            throw new LineColorLengthException("[ERROR] 노선 색은 20자 이하여야 합니다.");
        }
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
        return color;
    }

    public List<Station> getStations() {
        return sections.getStations();
    }
}
