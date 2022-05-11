package wooteco.subway.domain;

import wooteco.subway.exception.constant.BlankArgumentException;

import java.util.Objects;

import static wooteco.subway.util.StringUtils.isBlank;

public class Station {

    private final Long id;
    private final String name;

    public Station(Long id) {
        this(id, null);
    }

    public Station(String name) {
        this(null, name);
    }

    public Station(Long id, String name) {
        if (isBlank(name)) {
            throw new BlankArgumentException();
        }
        this.id = id;
        this.name = name;
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

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

