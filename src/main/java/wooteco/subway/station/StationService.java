package wooteco.subway.station;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.LineDuplicationException;
import wooteco.subway.exception.StationDuplicationException;
import wooteco.subway.line.Line;

import java.util.List;
import java.util.Optional;

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
        throw new LineDuplicationException();
    }

    public List<Station> stations() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
