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

    public Station save(Station station) {
        validateDuplicationName(station);
        return stationDao.save(station);
    }

    private void validateDuplicationName(Station station) {
        List<Station> stations = stationDao.findAll();
        if (stations.contains(station)) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public boolean deleteById(Long id) {
        return stationDao.deleteById(id);
    }
}
