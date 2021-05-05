package wooteco.subway.station;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StationService {

    private final StationDao stationDao;

    private StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(final StationRequest stationRequest) {
        final String name = stationRequest.getName();
        validateDuplicatedStationName(name);
        final Station station = StationDao.save(new Station(name));
        return new StationResponse(station.getId(), station.getName());
    }

    private void validateDuplicatedStationName(final String name) {
        StationDao.findByName(name)
            .ifPresent(station -> {
                throw new IllegalStateException("중복된 이름의 지하철역입니다.");
            });
    }

    public List<StationResponse> findStations() {
        final List<Station> stations = StationDao.findAll();
        return stations.stream()
            .map(it -> new StationResponse(it.getId(), it.getName()))
            .collect(Collectors.toList());
    }

    public Station findById(final Long id) {
        return StationDao.findById(id).
            orElseThrow(() -> new NoSuchElementException("해당 Id의 지하철역이 없습니다."));
    }

    public Station findByName(final String name) {
        return StationDao.findByName(name).
            orElseThrow(() -> new NoSuchElementException("해당 이름의 지하철역이 없습니다."));
    }

    public void deleteStation(final Long id) {
        findById(id);
        StationDao.deleteById(id);
    }
}
