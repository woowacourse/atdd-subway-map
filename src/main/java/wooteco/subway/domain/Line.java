package wooteco.subway.domain;

import wooteco.subway.exception.BlankArgumentException;

public class Line {

    private final Long id;
    private final String name;
    private final String color;

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        if (name.isBlank() || color.isBlank()) {
            throw new BlankArgumentException("노선의 이름과 색깔은 빈 문자열일 수 없습니다.");
        }
        this.id = id;
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

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }
}
