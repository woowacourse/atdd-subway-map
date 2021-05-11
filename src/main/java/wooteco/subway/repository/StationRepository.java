package wooteco.subway.repository;

import org.springframework.stereotype.Repository;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.station.Station;

@Repository
public class StationRepository {

    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station findById(Long id) {
        return stationDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 역입니다."));
    }
}
