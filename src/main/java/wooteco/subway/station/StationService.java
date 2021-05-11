package wooteco.subway.station;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.station.DuplicatedStationException;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.station.dao.StationDao;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationDao stationDao;

    @Transactional
    public Station save(Station station) {
        if (stationDao.existByName(station.getName())) {
            throw new DuplicatedStationException();
        }
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    @Transactional
    public void deleteStation(Long id) {
        stationDao.delete(id);
    }

    public Station findStation(Long id) {
        if (!stationDao.existById(id)) {
            throw new StationNotFoundException();
        }
        return stationDao.findById(id);
    }
}
