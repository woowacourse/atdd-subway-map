package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.exceptions.StationDuplicationException;
import wooteco.subway.exceptions.StationNotFoundException;
import wooteco.subway.repository.StationDao;
import wooteco.subway.domain.station.Station;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(String name) {
        validateDuplication(name);
        Station station = new Station(name);
        long id = stationDao.save(station);
        station.setId(id);
        return station;
    }

    public Station findById(long id) {
        return stationDao.findById(id)
            .orElseThrow(StationNotFoundException::new);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }

    private void validateDuplication(String name) {
        boolean isDuplicated = stationDao.findAll()
            .stream()
            .anyMatch(station -> station.getName().equals(name));
        if (isDuplicated) {
            throw new StationDuplicationException();
        }
    }
}
