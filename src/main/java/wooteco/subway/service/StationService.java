package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.CommonStationDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;

@Service
public class StationService {

    private final CommonStationDao stationDao;

    public StationService(final CommonStationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public Station save(final StationRequest stationRequest) {
        final Station station = new Station(stationRequest.getName());
        return stationDao.save(station);
    }


    @Transactional(readOnly = true)
    public List<Station> findAll() {
        return stationDao.findAll();
    }


    @Transactional
    public void deleteById(final Long id) {
        stationDao.deleteById(id);
    }
}
