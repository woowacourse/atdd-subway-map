package wooteco.subway.station.service;

import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.List;

public class StationService {

    public Station save(String stationName) {
        Station station = new Station(stationName);
        if (StationDao.findByName(stationName).isPresent()) {
            throw new IllegalArgumentException("같은 이름의 역이 있습니다;");
        }
        return StationDao.save(station);
    }

    public List<Station> findAll() {
        return StationDao.findAll();
    }

    public void delete(Long id) {
        StationDao.delete(id);
    }
}
