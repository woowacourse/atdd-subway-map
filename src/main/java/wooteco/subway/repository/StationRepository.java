package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.entity.StationEntity;
import wooteco.subway.domain.Station;

@Repository
public class StationRepository {

    private final StationDao stationDao;

    public StationRepository(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Long save(final Station station) {
        return stationDao.save(StationEntity.from(station));
    }

    public Station findById(final Long id) {
        final StationEntity entity = stationDao.findById(id);
        return entity.toStation();
    }

    public List<Station> findAll() {
        final List<StationEntity> entities = stationDao.findAll();
        return entities.stream()
                .map(StationEntity::toStation)
                .collect(Collectors.toList());
    }

    public void deleteById(final Long id) {
        stationDao.deleteById(id);
    }

    public boolean existsById(final Long id) {
        return stationDao.existsById(id);
    }
}
