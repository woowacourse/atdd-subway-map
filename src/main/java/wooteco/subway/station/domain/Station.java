package wooteco.subway.station.domain;

import java.util.Objects;

public class Station {
    private final Long id;
    private final String name;

    public Station(String name) {
        this(null, name);
    }

    public Station(Long id) {
        this(id, null);
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSame(Long stationId) {
        return id.equals(stationId);
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

