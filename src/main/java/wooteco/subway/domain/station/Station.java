package wooteco.subway.domain.station;

import java.util.Objects;

import wooteco.subway.domain.property.Name;
import wooteco.subway.util.Id;

public class Station {
    @Id
    private final Long id;
    private final Name name;

    public Station(Long id, Name name) {
        this.id = id;
        this.name = name;
    }

    public Station(Long id, String name) {
        this(id, new Name(name));
    }

    public Station(String name) {
        this(null, new Name(name));
    }

    public Station() {
        this(null);
    }

    public boolean hasSameNameWith(Station otherStation) {
        return this.name.equals(otherStation.name);
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
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

