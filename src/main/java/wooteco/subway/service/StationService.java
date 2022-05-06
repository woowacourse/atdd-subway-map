package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.NotFoundException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public Station save(final Station station) {
        if (stationDao.existByName(station.getName())) {
            throw new IllegalStateException("이미 존재하는 역 이름입니다.");
        }
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    @Transactional
    public void delete(final Long stationId) {
        validateExistStation(stationId);
        stationDao.delete(stationId);
    }

    public void validateExistStation(final Long stationId) {
        if (!stationDao.existById(stationId)) {
            throw new NotFoundException("존재하지 않는 Station입니다.");
        }
    }
}
