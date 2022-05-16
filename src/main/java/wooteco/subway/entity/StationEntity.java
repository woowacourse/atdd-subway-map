package wooteco.subway.entity;

public class StationEntity {

    private final Long id;
    private final String name;

    public static class Builder {

        private Long id;
        private final String name;

        public Builder(String name) {
            this.name = name;
        }

        public StationEntity.Builder id(Long id) {
            this.id = id;
            return this;
        }

        public StationEntity build() {
            return new StationEntity(this);
        }
    }

    private StationEntity(StationEntity.Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
