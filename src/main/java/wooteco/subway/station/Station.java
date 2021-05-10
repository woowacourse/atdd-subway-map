package wooteco.subway.station;

import java.util.Objects;
import wooteco.subway.exception.NotInputDataException;

public class Station {

    private Long id;
    private String name;

    public Station() {
    }

    public Station(String name) {
        this(0L, name);
    }

    public Station(Long id, String name) {
        validate(name);
        this.id = id;
        this.name = name.trim();
    }

    private void validate(String name) {
        if (Objects.isNull(name) || name.trim().length() == 0) {
            throw new NotInputDataException();
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
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

