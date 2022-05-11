package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Service
public class StationService {
    private static final String ALREADY_IN_STATION_ERROR_MESSAGE = "이미 해당 이름의 역이 있습니다.";

    private final StationDao stationDao;
    private final CheckService checkService;

    public StationService(StationDao stationDao, CheckService checkService) {
        this.stationDao = stationDao;
        this.checkService = checkService;
    }

    @Transactional
    public Station save(Station station) {
        validateUniqueName(station.getName());
        Long id = stationDao.save(station);
        return stationDao.findById(id);
    }

    private void validateUniqueName(String name) {
        if (stationDao.hasStation(name)) {
            throw new IllegalArgumentException(ALREADY_IN_STATION_ERROR_MESSAGE);
        }
    }

    public Station findById(Long id) {
        checkService.checkStationExist(id);
        return stationDao.findById(id);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }
}
