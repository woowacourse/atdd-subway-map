package wooteco.subway.station;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.StationNotFoundException;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationDao stationDao;

    public Station findStation(Long id) {
        return stationDao.findStationById(id).orElseThrow(StationNotFoundException::new);
    }
}
