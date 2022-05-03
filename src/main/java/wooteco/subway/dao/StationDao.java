package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;

import wooteco.subway.domain.Station;

public class StationDao {

    private static final List<Station> stations = new ArrayList<>();
    private static Long seq = 0L;

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public Station findByName(String name) {
        return stations.stream()
            .filter(station -> name.equals(station.getName()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public List<Station> findAll() {
        return List.copyOf(stations);
    }

    private Station createNewObject(Station station) {
        return new Station(++seq, station.getName());
    }
}
