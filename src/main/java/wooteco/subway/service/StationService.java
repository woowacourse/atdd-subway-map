package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateNameException;

import java.util.List;

@Transactional
@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(final Station station) {
        checkDuplicateStationName(station);
        return stationDao.save(station);
    }

    private void checkDuplicateStationName(final Station station) {
        boolean existsName = stationDao.findByName(station.getName()).isPresent();
        if (existsName) {
            throw new DuplicateNameException("이미 저장된 역 이름입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<Station> findAll() {
        return stationDao.findAll();
    }

    @Transactional(readOnly = true)
    public void delete(final Long id) {
        stationDao.delete(id);
    }
}
