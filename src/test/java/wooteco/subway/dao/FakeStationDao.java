package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import wooteco.subway.domain.Station;

public class FakeStationDao implements StationDao {

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
    public void deleteById(Long id) {
        if (!stations.removeIf(station -> station.getId().equals(id))) {
            throw new IllegalArgumentException("존재하지 않는 역 입니다.");
        }
    }
}
