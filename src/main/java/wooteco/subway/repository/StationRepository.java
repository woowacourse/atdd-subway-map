package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.StationSeries;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.RowNotFoundException;
import wooteco.subway.util.SimpleReflectionUtils;

@Repository
public class StationRepository {

    private final PersistManager<StationEntity> persistManager;
    private final StationDao stationDao;

    public StationRepository(PersistManager<StationEntity> persistManager, StationDao stationDao) {
        this.persistManager = persistManager;
        this.stationDao = stationDao;
    }

    public void persist(StationSeries stationSeries) {
        List<Long> persistedIds = toIds(findAllStations());
        final List<Station> stations = stationSeries.getStations();
        for (Station station : stations) {
            final StationEntity entity = StationEntity.from(station);
            final Long id = persistManager.persist(stationDao, entity, persistedIds);
            persistedIds.remove(id);
            SimpleReflectionUtils.injectId(station, id);
        }
        persistManager.deletePersistedAll(stationDao, persistedIds);
    }

    private List<Long> toIds(List<Station> stations) {
        return stations.stream()
            .map(Station::getId)
            .collect(Collectors.toList());
    }

    public Station findById(Long id) {
        final StationEntity entity = stationDao.findById(id)
            .orElseThrow(() -> new RowNotFoundException(String.format("%d의 id를 가진 역이 존재하지 않습니다.", id)));
        return new Station(entity.getId(), entity.getName());
    }

    public List<Station> findAllStations() {
        return stationDao.findAll()
            .stream()
            .map(entity -> new Station(entity.getId(), entity.getName()))
            .collect(Collectors.toList());
    }

    private void persistEach(List<Long> persistedIds, List<Station> stations) {
        for (Station station : stations) {
            final StationEntity entity = StationEntity.from(station);
            final Long id = persistManager.persist(stationDao, entity, persistedIds);
            SimpleReflectionUtils.injectId(station, id);
        }
    }
}
