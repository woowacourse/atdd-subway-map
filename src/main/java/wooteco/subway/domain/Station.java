package wooteco.subway.domain;

import java.util.Objects;

public class Station {
    private final Long id;
    private final Name name;

    public Station(String name) {
        this(null, name);
    }

    public Station(Long id, String name) {
        this(id, new Name(name));
    }

    public Station(Long id, Name name) {
        this.id = id;
        this.name = name;
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
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

