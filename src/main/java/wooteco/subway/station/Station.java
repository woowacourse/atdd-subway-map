package wooteco.subway.station;

import java.util.Objects;

public class Station {

    private StationId id;
    private String name;

    public Station() {
    }

    public Station(final String name) {
        this.name = name;
    }

    public Station(final Long id, final String name) {
        this.id = new StationId(id);
        this.name = name;
    }

    public Long getId() {
        if (id == null) {
            return null;
        }
        return id.getId();
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
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
