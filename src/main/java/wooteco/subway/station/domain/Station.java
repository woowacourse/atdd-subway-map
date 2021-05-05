package wooteco.subway.station.domain;

import java.util.Objects;

public class Station {
    private Long id;
    private StationName name;

    public Station(String name) {
        this(null, name);
    }

    public Station(Long id, final String name) {
        this(id, new StationName(name));
    }

    public Station(Long id, StationName name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public StationName getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

