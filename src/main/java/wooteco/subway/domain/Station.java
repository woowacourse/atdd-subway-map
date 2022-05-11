package wooteco.subway.domain;

import java.util.Objects;

public class Station {
    private Long id;
    private Name name;

    public Station(Long id, String name) {
        this.id = id;
        this.name = new Name(name);
    }

    public Station(String name) {
        this.name = new Name(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getName();
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

