package wooteco.subway.domain.station;

import java.util.Objects;

import wooteco.subway.domain.Id;

public class Station {

    private final Id id;
    private final StationName name;

    public Station(Id id, String name) {
        this.id = id;
        this.name = new StationName(name);
    }

    public Station(Long id, String name) {
        this(new Id(id), name);
    }

    public Station(String name) {
        this(new Id(), name);
    }

    public Long getId() {
        return id.getId();
    }

    public String getName() {
        return name.getName();
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
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Station{" + id +
                ", " + name +
                '}';
    }
}
