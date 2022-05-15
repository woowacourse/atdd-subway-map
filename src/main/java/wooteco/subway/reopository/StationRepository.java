package wooteco.subway.reopository;

import static java.util.stream.Collectors.toList;

import java.util.List;
import org.springframework.stereotype.Repository;
import wooteco.subway.reopository.Entity.StationEntity;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.reopository.dao.StationDao;

@Repository
public class StationRepository {

    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Long save(Station station) {
        return stationDao.save(new StationEntity(station.getName()));
    }

    public Station findById(Long id, String errorMessage) {
        StationEntity stationEntity = stationDao.findById(id)
                .orElseThrow(() -> new NotFoundException(errorMessage));
        return new Station(stationEntity.getId(), stationEntity.getName());
    }

    public List<Station> findAll() {
        List<StationEntity> list = stationDao.findAll();
        return list.stream().map(entity -> new Station(entity.getId(), entity.getName())).collect(toList());
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }

    public boolean existByName(String name) {
        return stationDao.existByName(name);
    }
}
