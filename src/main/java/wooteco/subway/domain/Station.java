package wooteco.subway.domain;

import java.util.Objects;
import wooteco.subway.exception.StationEmptyException;

public class Station {

    private Long id;
    private String name;

    public Station(String name) {
        this(null, name);
    }

    public Station(final Long id, final String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new StationEmptyException();
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
