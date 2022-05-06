package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Service
public class StationService {
    private static final String ALREADY_IN_STATION_ERROR_MESSAGE = "이미 해당 이름의 역이 있습니다.";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        try {
            stationDao.findByName(station.getName());
        } catch (NoSuchElementException exception) {
            return stationDao.save(station);
        }
        throw new IllegalStateException(ALREADY_IN_STATION_ERROR_MESSAGE);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
