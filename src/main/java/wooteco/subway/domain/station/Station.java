package wooteco.subway.domain.station;

import wooteco.subway.domain.station.value.StationId;
import wooteco.subway.domain.station.value.StationName;

import java.beans.ConstructorProperties;
import java.util.Objects;

public class Station {
    private StationId id;
    private StationName name;

    @ConstructorProperties({"id", "name"})
    public Station(StationId id, StationName name) {
        this.id = id;
        this.name = name;
    }

    public Station(StationName name) {
        this.name = name;
    }

    public Long getId() {
        return id.longValue();
    }

    public String getName() {
        return name.asString();
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

