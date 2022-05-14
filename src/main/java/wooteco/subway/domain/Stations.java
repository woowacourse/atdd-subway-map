package wooteco.subway.domain;

import wooteco.subway.exception.ClientException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stations {

    private final List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = new ArrayList<>(stations);
    }

    public void add(Station station) {
        validateDuplicateStation(station);
        stations.add(station);
    }

    private void validateDuplicateStation(Station request) {
        boolean duplicate = stations.stream()
                .anyMatch(station -> station.isSameName(request.getName()));

        if (duplicate) {
            throw new ClientException("이미 등록된 지하철역입니다.");
        }
    }

    public List<Station> getStations() {
        return Collections.unmodifiableList(stations);
    }
}
