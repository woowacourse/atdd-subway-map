package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public long save(Station station) {
        validateName(station);
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        int deletedRow = stationDao.delete(id);
        if (deletedRow == 0) {
            throw new IllegalArgumentException("존재하지 않는 지하철역입니다.");
        }
    }

    private void validateName(Station station) {
        if (stationDao.existStationByName(station.getName())) {
            throw new IllegalArgumentException("지하철역 이름이 중복됩니다.");
        }
    }
}
