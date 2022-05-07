package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;

@Service
public class StationService implements CommonStationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    @Override
    public Station save(final StationRequest stationRequest) {
        final Station station = new Station(stationRequest.getName());
        return stationDao.save(station);
    }


    @Transactional(readOnly = true)
    @Override
    public List<Station> findAll() {
        return stationDao.findAll();
    }


    @Transactional
    @Override
    public void deleteById(final Long id) {
        stationDao.deleteById(id);
    }
}
