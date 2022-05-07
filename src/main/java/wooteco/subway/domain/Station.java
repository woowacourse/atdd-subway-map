package wooteco.subway.domain;

import java.util.Objects;

public class Station {

    private static final  long EMPTY_ID = 0;
    private static final String EMPTY_NAME = "";

    private final Long id;
    private final String name;

    public Station() {
        this(EMPTY_ID, EMPTY_NAME);
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(EMPTY_ID, name);
    }

    public boolean isSameId(final long id) {
        return this.id.equals(id);
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
        if (!(o instanceof Station)) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                '}';
    }
}

