package wooteco.subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Service
public class StationService {

    private static final String DUPLICATE_STATION_NAME_EXCEPTION = "중복된 역 이름입니다.";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public Station createStation(String name) {
        if (stationDao.existsByName(name)) {
            throw new IllegalArgumentException(DUPLICATE_STATION_NAME_EXCEPTION);
        }
        return stationDao.save(new Station(name));
    }

    @Transactional(readOnly = true)
    public Station findStationById(Long id) {
        return stationDao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Station> showStations() {
        return stationDao.findAll();
    }

    @Transactional
    public void deleteStation(Long id) {
        stationDao.deleteById(id);
    }
}
