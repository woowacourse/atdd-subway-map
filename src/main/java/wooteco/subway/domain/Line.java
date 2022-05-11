package wooteco.subway.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Line {
    private Long id;
    private final String name;
    private final String color;

    public Line(String name, String color) {
        this(null, name, color);
    }

    public boolean hasSameName(Line line) {
        return name.equals(line.getName());
    }
}
