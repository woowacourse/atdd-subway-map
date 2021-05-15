package wooteco.subway.station.domain;

import java.util.List;

public class Stations {
    private final List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public boolean contains(Long id) {
        return stations.stream()
                .anyMatch(station -> station.isSameId(id));
    }
}
