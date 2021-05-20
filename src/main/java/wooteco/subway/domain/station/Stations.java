package wooteco.subway.domain.station;

import java.util.ArrayList;
import java.util.List;

public class Stations {
    private final List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = new ArrayList<>(stations);
    }

    public Station findStationById(long stationId) {
        return this.stations.stream()
                .filter(station -> station.getId() == stationId)
                .findFirst()
                .get();
    }
}
