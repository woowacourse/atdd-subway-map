package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;

@Service
public class SpringStationService implements StationService {

    private final StationDao stationDao;

    public SpringStationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    @Override
    public Station save(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        return stationDao.save(station);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Station> findAll() {
        return stationDao.findAll();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
