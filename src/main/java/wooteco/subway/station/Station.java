package wooteco.subway.station;

import wooteco.subway.exception.station.StationLengthException;
import wooteco.subway.exception.station.StationSuffixException;

import java.util.Objects;

public class Station {
    private static final String SUFFIX = "역";

    private Long id;
    private String name;

    public Station() {
    }

    public Station(String name) {
        this(null, name);
    }

    public Station(Long id, String name) {
        validateSuffix(name);
        validateLength(name);
        this.id = id;
        this.name = name;
    }

    private void validateSuffix(String name) {
        if (isNotEndsWithStation(name)) {
            throw new StationSuffixException();
        }
    }

    private boolean isNotEndsWithStation(String name) {
        return !name.endsWith(SUFFIX);
    }

    private void validateLength(String name) {
        if (name.length() < 2) {
            throw new StationLengthException();
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

