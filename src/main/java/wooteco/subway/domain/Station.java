package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.IllegalStationNameException;

public class Station {

    private final Long id;
    private final String name;

    public Station(final Long id, final String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    private void validateName(final String name) {
        if (name.equals(null) || name.isBlank()) {
            throw new IllegalStationNameException();
        }
    }

    public Station(final String name) {
        this(null, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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
        return name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

