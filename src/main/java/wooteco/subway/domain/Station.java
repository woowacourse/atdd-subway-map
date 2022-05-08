package wooteco.subway.domain;

import java.util.Objects;

public class Station {

    private static final long TEMPORARY_ID = 0L;

    private final Long id;
    private final Name name;

    public Station(String name) {
        this(TEMPORARY_ID, name);
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = new Name(name);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public boolean isSameName(String name) {
        return this.name.isSame(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Station station = (Station)o;
        return Objects.equals(getId(), station.getId()) && Objects.equals(getName(), station.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "Station{" +
            "id=" + id +
            ", name=" + name +
            '}';
    }
}

