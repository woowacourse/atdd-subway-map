package wooteco.subway.domain;

import java.util.Objects;

public class Station {

    private Long id;
    private String name;

    public static class Builder {

        private final String name;

        private Long id = null;

        public Builder(String name) {
            this.name = name;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Station build() {
            return new Station(this);
        }
    }

    private Station(Builder builder) {
        id = builder.id;
        name = builder.name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public boolean isSameName(Station other) {
        return name.equals(other.name);
    }
}

