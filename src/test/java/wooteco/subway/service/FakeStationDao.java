package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

class FakeStationDao implements StationDao {

    private final List<Station> stations = new ArrayList<>();
    private Long seq = 0L;

    @Override
    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private Station createNewObject(Station station) {
        return new Station(++seq, station.getName());
    }

    @Override
    public Optional<Station> findByName(String name) {
        return stations.stream()
            .filter(station -> name.equals(station.getName()))
            .findFirst();
    }

    @Override
    public Optional<Station> findById(Long id) {
        return stations.stream()
            .filter(station -> station.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<Station> findAll() {
        return List.copyOf(stations);
    }

    @Override
    public int deleteById(Long id) {
        if (!stations.removeIf(station -> station.getId().equals(id))) {
            return 0;
        }
        return 1;
    }
}
