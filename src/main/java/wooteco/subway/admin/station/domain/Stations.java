package wooteco.subway.admin.station.domain;

import java.util.Set;

public class Stations {
    private Set<Station> stations;

    public Stations(final Set<Station> stations) {
        this.stations = stations;
    }

    public Set<Station> getStations() {
        return stations;
    }

    public String findNameById(Long stationId) {
        return stations.stream()
                .filter(station -> station.isSameId(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(stationId + " : 일치하는 역이 없습니다."))
                .getName();
    }
}
