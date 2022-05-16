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

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Station(final String name) {
        this(EMPTY_ID, name);
    }

    public boolean isSameName(final Station target) {
        return this.name.equals(target.name);
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
        return id.equals(station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                '}';
    }
}

