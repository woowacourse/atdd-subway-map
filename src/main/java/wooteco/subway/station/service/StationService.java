package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.exception.notfoundexception.NotFoundStationException;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        if (stationDao.findStationByName(station.getName()).isPresent()) {
            throw new DuplicatedNameException();
        }
        return stationDao.save(station);
    }

    public List<Station> findByIds(List<Long> ids) {
        return ids.stream()
           .map(id -> stationDao.findById(id).orElseThrow(NotFoundStationException::new))
           .collect(Collectors.toList());
    }

    public Station findById(Long id) {
        return stationDao.findById(id).orElseThrow(NotFoundStationException::new);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        stationDao.findById(id).orElseThrow(NotFoundStationException::new);
        stationDao.delete(id);
    }
}
