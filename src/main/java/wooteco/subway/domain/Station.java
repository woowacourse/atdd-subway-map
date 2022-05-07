package wooteco.subway.domain;

import java.util.Objects;

public class Station {

    private Long id;
    private final Name name;

    public Station(final String name) {
        this.name = new Name(name);
    }

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = new Name(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Station{");
        sb.append("name=").append(name);
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}

