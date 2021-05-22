package wooteco.subway.station;

import java.util.Objects;
import wooteco.subway.StringInput;

public class Station {

    private final Long id;
    private final StringInput name;

    public Station(String name) {
        this(0L, name);
    }

    public Station(Long id, String name) {
        this.id = id;
        this.name = new StringInput(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getItem();
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

