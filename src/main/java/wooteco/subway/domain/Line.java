package wooteco.subway.domain;

public class Line {
    private Long id;
    private String name;
    private String color;

    public Line(final String name, final String color) {
        this(null, name, color);
    }

    public Line(final Long id, final String name, final String color) {
        validateNullOrEmpty(id, name, color);
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private void validateNullOrEmpty(final Long id, final String name, final String color) {
        if (id == null) {
            throw new NullPointerException();
        }
        if (name.isEmpty() || color.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getId() {
        return id;
    }
}
