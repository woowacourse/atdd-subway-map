package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Stations {

    private final List<Station> stations;

    public Stations(final List<Station> stations) {
        this.stations = new ArrayList<>(stations);
    }

    public List<Station> getStations() {
        return stations;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Stations stations1 = (Stations) o;
        return Objects.equals(stations, stations1.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations);
    }
}
