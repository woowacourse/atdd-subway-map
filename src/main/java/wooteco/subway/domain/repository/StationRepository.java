package wooteco.subway.domain.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.station.Station;

import java.util.List;

@Repository
public class StationRepository {
    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public long insertStation(Station station) {
        return stationDao.insert(station);
    }

    public List<Station> findStations() {
        return stationDao.selectAll();
    }

    public void deleteStation(Long id) {
        stationDao.delete(id);
    }
}
