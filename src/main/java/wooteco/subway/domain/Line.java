package wooteco.subway.domain;

public class Line {

    private Long id;
    private String name;
    private String color;

    public static class Builder {

        private final String name;
        private final String color;

        private Long id = null;

        public Builder(String name, String color) {
            this.name = name;
            this.color = color;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Line build() {
            return new Line(this);
        }
    }

    private Line(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.color = builder.color;
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

    public boolean isSameName(Line line) {
        return name.equals(line.name);
    }

    public boolean isSameColor(Line line) {
        return color.equals(line.color);
    }
}
