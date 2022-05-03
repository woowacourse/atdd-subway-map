package wooteco.subway.service;

import java.util.List;
import java.util.Optional;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        Optional<Station> foundStation = stationDao.findByName(station.getName());
        if (foundStation.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 역입니다.");
        }
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }
}
