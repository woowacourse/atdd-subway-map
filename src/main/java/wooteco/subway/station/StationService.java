package wooteco.subway.station;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.station.DuplicatedStationException;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.station.dao.StationDao;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationDao stationDao;

    public Station save(Station station) {
        if(stationDao.findStationByName(station.getName()).isPresent()) {
            throw new DuplicatedStationException();
        }
        return stationDao.save(station);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void deleteStation(Long id) {
        stationDao.delete(id);
    }

    public Station findStation(Long id) {
        return stationDao.findStationById(id).orElseThrow(StationNotFoundException::new);
    }
}
