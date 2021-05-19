package wooteco.subway.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.EntityNotFoundException;

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

    @Transactional(readOnly = true)
    void checkDuplicateStationName(final Station station) {
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
    public Station findById(Long id) {
        return stationDao.findById(id)
                         .orElseThrow(() -> new EntityNotFoundException("해당 ID와 일치하는 역이 존재하지 않습니다."));
    }

    public void delete(final Long id) {
        stationDao.delete(id);
    }
}
