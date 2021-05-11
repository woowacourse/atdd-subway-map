package wooteco.subway.domain;


import wooteco.subway.exception.SubwayIllegalArgumentException;

import java.util.Objects;

public class Line {
    private final Long id;
    private final String name;
    private final String color;

    private Line(Long id, String name, String color) {
        validateArguments(name, color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateArguments(String name, String color) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(color);
        if(name.length() < 1){
            throw new SubwayIllegalArgumentException("노선의 이름은 1글자 이상이어야 합니다.");
        }
        if(color.length() < 1){
            throw new SubwayIllegalArgumentException("노선의 색상을 잘못 지정하셨습니다.");
        }
    }

    public static Line of(Long id, String name, String color) {
        return new Line(id, name, color);
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

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }
}
