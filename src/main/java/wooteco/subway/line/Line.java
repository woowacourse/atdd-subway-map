package wooteco.subway.line;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Line {

    private Long id;
    private String name;
    private String color;

    public static Line create(String name, String color) {
        return create(null, name, color);
    }

    public static Line create(Long id, String name, String color) {
        return new Line(id, name, color);
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public void changeInfo(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public boolean isNotSameId(Long id) {
        return !this.id.equals(id);
    }

    public boolean isSameColor(String color) {
        return this.color.equals(color);
    }
}
