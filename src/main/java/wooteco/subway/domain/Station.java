package wooteco.subway.domain;

import java.util.Objects;

public class Station {

    private final Long id;
    private final String name;

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Station createWithoutId(final String name) {
        return new Station(null, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Station)) {
            return false;
        }
        final Station station = (Station) o;
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

