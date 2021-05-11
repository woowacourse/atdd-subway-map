package wooteco.subway.station;

import java.util.Objects;

import wooteco.subway.exception.IllegalInputException;

public class Station {
    private Long id;
    private String name;

    public Station() {
    }

    public Station(String name) {
        validateName(name);
        this.name = name;
    }

    public Station(Long id, String name) {
        this(name);
        this.id = id;
    }

    public Station(long id, Station station) {
        this(id, station.getName());
    }

    private void validateName(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalInputException();
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Station station = (Station)o;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

