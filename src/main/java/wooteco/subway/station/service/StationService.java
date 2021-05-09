package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.List;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Long save(Station station) {
        if (stationDao.countStationByName(station.getName()) > 0) {
            throw new IllegalArgumentException("중복된 지하철 역입니다.");
        }
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }

    public Station findStationById(Long id) {
        return stationDao.findStationById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 지하철 역이 존재하지 않습니다."));
    }
}
