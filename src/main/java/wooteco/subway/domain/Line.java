package wooteco.subway.domain;

public class Line {
    private Long id;
    private String name;
    private String color;

    public Line(Long id, String name, String color) {
        validateNameSize(name);
        validateColorSize(color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color) {
        validateNameSize(name);
        validateColorSize(color);
        this.name = name;
        this.color = color;
    }

    private void validateNameSize(String name) {
        if (name == null || name.isBlank() || name.length() > 255) {
            throw new IllegalArgumentException("존재할 수 없는 이름입니다.");
        }
    }

    private void validateColorSize(String color) {
        if (color == null || color.isBlank() || color.length() > 20) {
            throw new IllegalArgumentException("존재할 수 없는 색상입니다.");
        }
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
