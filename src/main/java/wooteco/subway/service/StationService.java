package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.station.Station;

import java.util.List;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(String name) {
        validateDuplication(name);
        Station station = new Station(name);
        long id = stationDao.save(station);
        return stationDao.findById(id);
    }

    private void validateDuplication(String name) {
        boolean isDuplicated = stationDao.findAll()
                .stream()
                .anyMatch(station -> station.hasSameName(name));
        if (isDuplicated) {
            throw new IllegalArgumentException("중복된 이름의 역이 존재합니다.");
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void deleteById(long id) {
        stationDao.deleteById(id);
    }
}
