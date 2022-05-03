package wooteco.subway.application;

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

    public Station createStation(String name) {
        if (stationDao.existsByName(name)) {
            throw new IllegalArgumentException("중복된 역 이름입니다.");
        }
        return stationDao.save(new Station(name));
    }

    public List<Station> showStations() {
        return stationDao.findAll();
    }

    public void deleteStation(Long id) {
        stationDao.deleteById(id);
    }
}
