package wooteco.subway.service;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
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

    public Station save(StationRequest stationRequest) {
        try {
            stationDao.findByName(stationRequest.getName());
        } catch (EmptyResultDataAccessException e) {
            Station station = new Station(stationRequest.getName());
            Long id = stationDao.save(station);

            return new Station(id, stationRequest.getName());
        }
        throw new IllegalArgumentException("중복된 ID가 존재합니다");
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
