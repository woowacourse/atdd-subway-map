package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.notfound.StationNotFoundException;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.StationEntity;

@Repository
public class StationRepository {

    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station findById(Long id) {
        return stationDao.findById(id)
                .map(this::entityToStation)
                .orElseThrow(StationNotFoundException::new);
    }

    public List<Station> findAll() {
        return stationDao.findAll().stream()
                .map(this::entityToStation)
                .collect(Collectors.toList());
    }

    private Station entityToStation(StationEntity entity) {
        return new Station(entity.getId(), entity.getName());
    }

    public Integer deleteById(Long id) {
        return stationDao.deleteById(id);
    }

    public Station save(Station station) {
        return entityToStation(stationDao.save(StationEntity.from(station)));
    }
}
