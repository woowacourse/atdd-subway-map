package wooteco.subway.station;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.LineService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StationService {

    private final StationDao stationDao;
    private final LineService lineService;

    public StationService(StationDao stationDao, LineService lineService) {
        this.stationDao = stationDao;
        this.lineService = lineService;
    }

    public StationResponse createStation(String stationName) {
        final Optional<Station> stationWithSameName = stationDao.findByName(stationName);
        if (stationWithSameName.isPresent()) {
            throw new IllegalArgumentException("역 이름이 중복됩니다");
        }
        Station station = stationDao.save(stationName);
        return StationResponse.from(station);
    }

    public List<StationResponse> showStations() {
        final List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteStation(long stationId) {
        final Station station = stationDao.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id에 대응하는 역이 없습니다."));
        deleteStationInEveryLine(station);
        stationDao.delete(station.getId());
    }

    private void deleteStationInEveryLine(Station station) {
        lineService.deleteStationInEveryLine(station);
    }
}
