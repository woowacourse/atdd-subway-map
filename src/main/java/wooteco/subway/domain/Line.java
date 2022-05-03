package wooteco.subway.domain;

public class Line {

    private Long id;
    private String name;
    private String color;

    public Line(final String name, final String color) {
        validateName(name);
        validateColor(color);
        this.name = name;
        this.color = color;
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("노선 이름은 공백일 수 없습니다.");
        }
    }

    private void validateColor(final String color) {
        if (color.isBlank()) {
            throw new IllegalArgumentException("색상이 공백일 수 없습니다.");
        }
    }

    public boolean isSameId(final Long id) {
        return this.id.equals(id);
    }

    public boolean isSameName(final String name) {
        return this.name.equals(name);
    }

    public boolean isSameColor(final String color) {
        return this.color.equals(color);
    }

    public void update(final String updateName, final String updateColor) {
        this.name = updateName;
        this.color = updateColor;
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
