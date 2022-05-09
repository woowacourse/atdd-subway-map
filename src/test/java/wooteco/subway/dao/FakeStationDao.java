package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import wooteco.subway.domain.Station;
import wooteco.subway.entity.StationEntity;

public class FakeStationDao implements StationDao{

    private Long seq = 0L;
    private final List<StationEntity> stationEntities = new ArrayList<>();

    @Override
    public StationEntity save(Station station) {
        StationEntity persistStationEntity = createStation(station);
        stationEntities.add(persistStationEntity);
        return persistStationEntity;
    }

    private StationEntity createStation(Station station) {
        return new StationEntity(++seq, station.getName());
    }

    @Override
    public List<StationEntity> findAll() {
        return stationEntities;
    }

    @Override
    public boolean deleteById(Long id) {
        return stationEntities.removeIf(it -> it.getId().equals(id));
    }

    @Override
    public boolean existsByName(String name) {
        Optional<StationEntity> station = stationEntities.stream()
            .filter(i -> i.getName().equals(name))
            .findAny();
        return station.isPresent();
    }
}
