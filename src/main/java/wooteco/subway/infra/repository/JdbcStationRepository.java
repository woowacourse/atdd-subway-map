package wooteco.subway.infra.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;
import wooteco.subway.infra.dao.StationDao;
import wooteco.subway.infra.dao.entity.StationEntity;

@Repository
public class JdbcStationRepository implements StationRepository {

    private final StationDao stationDao;

    public JdbcStationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Override
    public Station save(Station station) {
        final StationEntity saved = stationDao.save(new StationEntity(station.getName()));
        return toStation(saved);
    }

    @Override
    public List<Station> findAll() {
        final List<StationEntity> stations = stationDao.findAll();

        return stations.stream()
                .map(this::toStation)
                .collect(Collectors.toList());
    }

    private Station toStation(StationEntity entity) {
        return new Station(entity.getId(), entity.getName());
    }

    @Override
    public Optional<Station> findById(Long id) {
        final Optional<StationEntity> stationEntity = stationDao.findById(id);

        if (stationEntity.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(toStation(stationEntity.get()));
    }

    @Override
    public boolean existByName(String name) {
        return stationDao.existsByName(name);
    }

    @Override
    public boolean existById(Long id) {
        return stationDao.existsById(id);
    }

    @Override
    public long deleteById(Long id) {
        return stationDao.deleteById(id);
    }
}
