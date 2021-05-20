package wooteco.subway.station.domain;

import java.util.Objects;

public class Station {
    private Long id;
    private StationName name;

    public Station() {
    }

    public Station(final Long id) {
        this(id, StationName.emptyName());
    }

    public Station(final String name) {
        this(null, new StationName(name));
    }

    public Station(final Long id, final String name) {
        this(id, new StationName(name));
    }

    public Station(final Long id, final StationName name) {
        this.id = id;
        this.name = name;
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name.name();
    }

    public boolean sameName(final String name) {
        return this.name.sameName(name);
    }

    public boolean sameId(final Long id) {
        return this.id.equals(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

