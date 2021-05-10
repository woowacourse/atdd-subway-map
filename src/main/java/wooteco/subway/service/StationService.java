package wooteco.subway.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.badRequest.StationNameDuplicatedException;
import wooteco.subway.domain.Station;
import wooteco.subway.dao.station.StationDao;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationService {

    private final StationDao stationDao;

    @Transactional
    public Station createStation(Station station) {
        if (stationDao.findStationByName(station.getName()).isPresent()) {
            throw new StationNameDuplicatedException();
        }

        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    @Transactional
    public void remove(Long id) {
        stationDao.remove(id);
    }
}
