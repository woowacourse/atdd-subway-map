package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;

public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(StationRequest stationRequest) {
        Optional<Station> findStation = stationDao.findByName(stationRequest.getName());
        if (findStation.isPresent()) {
            throw new IllegalArgumentException("중복된 ID가 존재합니다");
        }
        Station station = new Station(stationRequest.getName());
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }

    public void deleteAll() {
        stationDao.deleteAll();
    }
}
