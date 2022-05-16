package wooteco.subway.entity;

public class LineEntity {

    private final Long id;
    private final String name;
    private final String color;

    public static class Builder {

        private Long id;
        private final String name;
        private final String color;

        public Builder(String name, String color) {
            this.name = name;
            this.color = color;
        }

        public LineEntity.Builder id(Long id) {
            this.id = id;
            return this;
        }

        public LineEntity build() {
            return new LineEntity(this);
        }
    }

    private LineEntity(LineEntity.Builder builder) {
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
}
