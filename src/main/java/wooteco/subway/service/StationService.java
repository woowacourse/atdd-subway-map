package wooteco.subway.service;

import java.util.List;
import java.util.Optional;

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
        Optional<Station> foundStation = stationDao.findByName(station.getName());
        if (foundStation.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 역입니다.");
        }
        return stationDao.save(station);
    }

    public Station findById(Long id) {
        Optional<Station> foundStation = stationDao.findById(id);
        if (foundStation.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 역입니다.");
        }
        return foundStation.get();
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
