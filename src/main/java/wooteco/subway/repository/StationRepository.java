package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.entity.StationEntity;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.NotFoundException;

@Repository
public class StationRepository {

    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        StationEntity saveEntity = stationDao.save(new StationEntity(station.getName()));
        return toStation(saveEntity);
    }

    public Station findById(Long id) {
        StationEntity stationEntity = stationDao.findById(id)
            .orElseThrow(() -> new NotFoundException("조회하려는 id가 존재하지 않습니다. id : " + id));
        return toStation(stationEntity);
    }

    public List<Station> findAll() {
        List<StationEntity> entities = stationDao.findAll();
        return entities.stream()
            .map(this::toStation)
            .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }

    public boolean existByName(String name) {
        return stationDao.existByName(name);
    }

    private Station toStation(StationEntity entity) {
        return new Station(entity.getId(), entity.getName());
    }
}
