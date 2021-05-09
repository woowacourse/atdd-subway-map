package wooteco.subway.domain;

import org.springframework.lang.NonNull;

public class Line {

    private final Long id;
    @NonNull
    private final String name;
    @NonNull
    private final String color;

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        this(null, name, color);
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
