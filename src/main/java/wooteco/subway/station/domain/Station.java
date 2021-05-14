package wooteco.subway.station.domain;

import wooteco.subway.exception.EmptyInputException;
import wooteco.subway.exception.NullInputException;
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
        validate(name);
        this.id = id;
        this.name = name;
    }

    private void validate(String name) {
        validateNotNull(name);
        validateSuffix(name);
        validateNotEmpty(name);
    }

    private void validateNotNull(String name) {
        if (name == null) {
            throw new NullInputException();
        }
    }

    private void validateNotEmpty(String name) {
        if ("".equals(name.substring(0, name.length()-1))) {
            throw new EmptyInputException();
        }
    }

    private void validateSuffix(String name) {
        if (isNotEndsWithStation(name)) {
            throw new StationSuffixException();
        }
    }

    private boolean isNotEndsWithStation(String name) {
        return !name.endsWith(SUFFIX);
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
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

