package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.StationEntity;

class FakeStationDao implements StationDao {

    private final List<StationEntity> stations = new ArrayList<>();
    private Long seq = 0L;

    @Override
    public StationEntity save(StationEntity station) {
        StationEntity persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private StationEntity createNewObject(StationEntity station) {
        return new StationEntity(++seq, station.getName());
    }

    @Override
    public Optional<StationEntity> findByName(String name) {
        return stations.stream()
            .filter(station -> name.equals(station.getName()))
            .findFirst();
    }

    @Override
    public Optional<StationEntity> findById(Long id) {
        return stations.stream()
            .filter(station -> station.getId().equals(id))
            .findFirst();
    }

    @Override
    public List<StationEntity> findAll() {
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
