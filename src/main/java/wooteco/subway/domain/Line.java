package wooteco.subway.domain;

import lombok.Getter;

@Getter
public class Line {
    private final Long id;
    private final String name;
    private final String color;

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }
}
