package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station create(Station station) {
        Optional<Station> wrappedStation = stationDao.findByName(station.getName());
        if (wrappedStation.isPresent()) {
            throw new IllegalArgumentException("이미 같은 이름의 지하철역이 존재합니다.");
        }
        return stationDao.save(station);
    }

    public List<Station> queryAll() {
        return stationDao.findAll();
    }

    public void remove(Long id) {
        Optional<Station> wrappedStation = stationDao.findById(id);
        if (wrappedStation.isEmpty()) {
            throw new IllegalArgumentException("해당 지하철역이 존재하지 않습니다.");
        }
        stationDao.deleteById(id);
    }
}
