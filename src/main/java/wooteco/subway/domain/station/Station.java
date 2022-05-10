package wooteco.subway.domain.station;

import java.util.Objects;

public class Station {

    private static final long TEMPORARY_ID = 0L;

    private final Long id;
    private final StationName name;

    public Station(Long id, String name) {
        this.id = id;
        this.name = new StationName(name);
    }

    public Station(String name) {
        this(TEMPORARY_ID, name);
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
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }
}
