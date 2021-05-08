package wooteco.subway.station;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.station.StationNameDuplicatedException;
import wooteco.subway.station.dao.StationDao;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationDao stationDao;

    public Station createStation(Station station) {
        if (stationDao.findStationByName(station.getName()).isPresent()) {
            throw new StationNameDuplicatedException();
        }

        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void remove(Long id) {
        stationDao.remove(id);
    }
}
