package wooteco.subway.station;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.NoStationException;

import java.util.List;

@Service
public class StationService {

    private final StationDao stationDao;

    private StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station add(StationRequest stationRequest) {
        return stationDao.save(new Station(stationRequest.getName()));
    }

    public List<Station> stations() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }

    public StationResponse findById(Long id) {
        return new StationResponse(
                stationDao.findById(id)
                        .orElseThrow(NoStationException::new)
        );
    }
}
