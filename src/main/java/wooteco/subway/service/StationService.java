package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.station.DuplicateStationNameException;
import wooteco.subway.exception.station.NoSuchStationException;

import java.util.List;
import java.util.Optional;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        validateDuplicateName(station);
        return stationDao.save(station);
    }

    private void validateDuplicateName(Station station) {
        Optional<Station> optionalStation = stationDao.findByName(station.getName());
        optionalStation.ifPresent(existed -> {
            throw new DuplicateStationNameException();
        });
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Station findById(Long id) {
        Optional<Station> station = stationDao.findById(id);
        return station.orElseThrow(NoSuchStationException::new);
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}

