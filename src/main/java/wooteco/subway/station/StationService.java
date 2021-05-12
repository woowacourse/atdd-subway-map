package wooteco.subway.station;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.station.StationDuplicatedException;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.station.dao.StationDao;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationDao stationDao;

    public Station find(Long id) {
        validateExistStation(id);

        return stationDao.findById(id);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    @Transactional
    public Station save(Station station) {
        validateExistName(station);

        return stationDao.create(station);
    }

    @Transactional
    public void remove(Long id) {
        stationDao.remove(id);
    }

    private void validateExistStation(Long id) {
        if (!stationDao.existById(id)) {
            throw new StationNotFoundException();
        }
    }

    private void validateExistName(Station station) {
        if (stationDao.existByName(station.getName())) {
            throw new StationDuplicatedException();
        }
    }
}
