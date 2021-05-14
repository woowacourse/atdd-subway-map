package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.line.NoLineException;
import wooteco.subway.exception.station.NoStationException;
import wooteco.subway.exception.station.StationDuplicationException;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationRequest;

import java.util.List;

@Service
public class StationService {

    private final StationDao stationDao;

    private StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station add(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        validateDuplicatedName(station.getName());
        return stationDao.save(station);
    }

    private void validateDuplicatedName(String name) {
        stationDao.findByName(name)
            .ifPresent(this::throwDuplicationException);
    }

    private void throwDuplicationException(Station station) {
        throw new StationDuplicationException();
    }

    public List<Station> stations() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        validateId(id);
        stationDao.delete(id);
    }

    public void validateId(Long stationId) {
        stationDao.findById(stationId)
            .orElseThrow(NoLineException::new);
    }

    public Station findById(Long id) {
        return stationDao.findById(id)
            .orElseThrow(NoStationException::new);
    }
}
