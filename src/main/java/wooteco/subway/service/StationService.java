package wooteco.subway.service;


import java.util.List;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class StationService {

    public void save(String stationName) {
        List<Station> stations = StationDao.findAll();
        for (Station station : stations) {
            if (station.isSameName(stationName)) {
                throw new IllegalArgumentException("이미 존재하는 역 이름입니다.");
            }
        }

        StationDao.save(new Station(stationName));
    }

    public List<Station> findAll() {
        return StationDao.findAll();
    }

    public void delete(Long stationId) {
        StationDao.delete(stationId);
    }
}
