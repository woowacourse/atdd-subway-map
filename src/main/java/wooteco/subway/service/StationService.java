package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

import java.util.List;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public Station create(Station station) {
        if (checkExistByName(station.getName())) {
            throw new IllegalArgumentException("이미 같은 이름의 지하철역이 존재합니다.");
        }
        return stationDao.save(station);
    }

    @Transactional(readOnly = true)
    public List<Station> getAll() {
        return stationDao.findAll();
    }

    @Transactional
    public void remove(Long id) {
        if (!checkExistById(id)) {
            throw new IllegalArgumentException("해당 지하철역이 존재하지 않습니다.");
        }
        stationDao.deleteById(id);
    }

    private boolean checkExistByName(String name) {
        return stationDao.findByName(name).isPresent();
    }

    private boolean checkExistById(Long id) {
        return stationDao.findById(id).isPresent();
    }

    @Transactional(readOnly = true)
    public List<Station> getAllByLineId(final long lineId) {
        return stationDao.findAllByLineId(lineId);
    }
}
