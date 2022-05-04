package wooteco.subway.domain;

import java.util.Objects;

public class Line {

    private Long id;
    private String name;
    private String color;

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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
}
