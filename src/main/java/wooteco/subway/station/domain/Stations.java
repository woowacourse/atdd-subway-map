package wooteco.subway.station.domain;

import wooteco.subway.section.domain.Section;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Stations {
    private final List<Station> stations;

    public Stations(final List<Station> stations) {
        this.stations = stations;
    }

    public List<Station> toList() {
        return Collections.unmodifiableList(stations);
    }

    public boolean containsAll(final Section section) {
        return stations.contains(section.getUpStation()) && stations.contains(section.getDownStation());
    }

    public boolean containsNone(final Section section) {
        return !stations.contains(section.getUpStation()) && !stations.contains(section.getDownStation());
    }

    public boolean doesNameExist(final String name) {
        return stations.stream().anyMatch(station -> station.hasName(name));
    }

    public boolean doesIdNotExist(final Long id) {
        return stations.stream().noneMatch(station -> station.hasId(id));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Stations stations1 = (Stations) o;
        return Objects.equals(stations, stations1.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations);
    }
}
