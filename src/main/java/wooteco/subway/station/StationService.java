package wooteco.subway.station;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.station.StationNameDuplicatedException;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.station.dao.StationDao;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public Station create(Station station) {
        validateExistByName(station);
        return stationDao.save(station);
    }

    public List<Station> showAll() {
        return stationDao.findAll();
    }

    @Transactional
    public void remove(Long id) {
        validateExistById(id);
        stationDao.remove(id);
    }

    private void validateExistById(Long id) {
        if (!stationDao.existById(id)) {
            throw new StationNotFoundException();
        }
    }

    private void validateExistByName(Station station) {
        if (stationDao.existByName(station.getName())) {
            throw new StationNameDuplicatedException();
        }
    }


}
