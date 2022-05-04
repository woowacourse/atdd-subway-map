package wooteco.subway.service;

import java.util.List;
import java.util.Optional;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class StationService {

    private static final StationService INSTANCE = new StationService(StationDao.getInstance());
    private final StationDao stationDao;

    private StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public static StationService getInstance() {
        return INSTANCE;
    }

    public Station save(Station station) {
        Optional<Station> foundStation = stationDao.findByName(station.getName());
        if (foundStation.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 역입니다.");
        }
        return stationDao.save(station);
    }

    public Station findById(Long id) {
        Optional<Station> station = stationDao.findById(id);
        if (station.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 역입니다.");
        }
        return station.get();
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
