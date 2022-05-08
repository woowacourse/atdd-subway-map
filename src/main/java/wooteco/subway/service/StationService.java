package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        validateDuplicateName(station);
        return stationDao.save(station);
    }

    private void validateDuplicateName(Station station) {
        Optional<Station> optionalStation = stationDao.findByName(station.getName());
        if (optionalStation.isPresent()) {
            throw new IllegalArgumentException("같은 이름의 역은 등록할 수 없습니다.");
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}

