package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import wooteco.subway.domain.Station;

public class StationDao {

    private static final StationDao INSTANCE = new StationDao();
    private static final List<Station> stations = new ArrayList<>();
    private static Long seq = 0L;

    private StationDao() {
    }

    public static StationDao getInstance() {
        return INSTANCE;
    }

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private Station createNewObject(Station station) {
        return new Station(++seq, station.getName());
    }

    public Optional<Station> findByName(String name) {
        return stations.stream()
            .filter(station -> name.equals(station.getName()))
            .findFirst();
    }

    public List<Station> findAll() {
        return List.copyOf(stations);
    }

    public void deleteByName(String name) {
        if (!stations.removeIf(station -> station.getName().equals(name))) {
            throw new IllegalArgumentException("해당 역 이름이 존재하지 않습니다.");
        }
    }

    public void deleteAll() {
        stations.clear();
    }
}
