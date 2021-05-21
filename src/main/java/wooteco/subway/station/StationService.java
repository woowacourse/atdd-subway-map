package wooteco.subway.station;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.station.StationDuplicationException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public Station add(StationRequest stationRequest) {
        validateDuplicatedName(stationRequest.getName());
        return stationDao.save(new Station(stationRequest.getName()));
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

    @Transactional
    public void delete(Long id) {
        stationDao.delete(id);
    }
}
