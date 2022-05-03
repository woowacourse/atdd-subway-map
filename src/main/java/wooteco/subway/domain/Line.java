package wooteco.subway.domain;

public class Line {

    private Long id;
    private final String name;
    private final String color;

    public Line(String name, String color) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("노선의 이름이 공백이 되어서는 안됩니다.");
        }
        this.name = name;
        this.color = color;
    }

    public boolean isSameName(final Line line) {
        return name.equals(line.name);
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
