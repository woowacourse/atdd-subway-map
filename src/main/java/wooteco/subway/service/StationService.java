package wooteco.subway.service;

import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.common.exception.NotFoundException;
import wooteco.subway.common.exception.SubwayHttpException;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.dao.StationDao;

@Service
@Transactional
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station add(Station station) {
        Long id = addStation(station);
        return findById(id);
    }

    private Long addStation(Station station) {
        try {
            return stationDao.save(station);
        } catch (DuplicateKeyException e) {
            throw new SubwayHttpException("중복된 역 이름입니다");
        }
    }

    private Station findById(Long id) {
        try {
            return stationDao.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("존재하지 않는 역입니다");
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
