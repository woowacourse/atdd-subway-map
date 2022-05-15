package wooteco.subway.domain;

public class Line {
    private static final int NAME_MAX_LENGTH = 255;
    private static final int COLOR_MAX_LENGTH = 20;

    private Long id;
    private String name;
    private String color;

    public Line() {
    }

    public Line(final Long id, final String name, final String color) {
        validateName(name);
        validateColor(color);

        this.id = id;
        this.name = name;
        this.color = color;
    }

    public Line(final String name, final String color) {
        this(null, name, color);
    }

    public void update(final String name, final String color) {
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

    private void validateName(String name) {
        if (name.isBlank() || name.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("노선 이름의 길이는 1 이상 " + NAME_MAX_LENGTH + " 이하여야 합니다.");
        }
    }

    private void validateColor(String color) {
        if (color.isBlank() || color.length() > COLOR_MAX_LENGTH) {
            throw new IllegalArgumentException("노선 색의 길이는 1 이상 " + COLOR_MAX_LENGTH + " 이하여야 합니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Line line = (Line) o;

        return id != null ? id.equals(line.id) : line.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
