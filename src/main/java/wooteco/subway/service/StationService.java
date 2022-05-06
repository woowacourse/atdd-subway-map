package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(String name) {
        validDuplicatedStation(name);
        Long id = stationDao.save(name);
        return new Station(id, name);
    }

    private void validDuplicatedStation(String name) {
        if (stationDao.existByName(name)) {
            throw new IllegalArgumentException("중복된 Station 이 존재합니다.");
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
