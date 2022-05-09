package wooteco.subway.repository.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import wooteco.subway.domain.station.Station;

public class FakeStationDao implements StationDao {

    private final List<Station> stations = new ArrayList<>();
    private Long seq = 0L;

    @Override
    public Long save(Station station) {
        Station newStation = new Station(++seq, station.getName());
        stations.add(newStation);
        return newStation.getId();
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    @Override
    public Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findAny();
    }

    @Override
    public Boolean existsByName(String name) {
        return stations.stream()
                .anyMatch(station -> name.equals(station.getName()));
    }

    @Override
    public void remove(Long id) {
        stations.remove(findById(id).get());
    }
}
