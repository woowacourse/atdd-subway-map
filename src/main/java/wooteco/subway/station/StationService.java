package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicatedNameException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(final StationRequest stationRequest) {
        final String name = stationRequest.getName();
        validateDuplicatedStationName(name);
        final Station station = stationDao.save(new Station(name));
        return new StationResponse(station.getId(), station.getName());
    }

    private void validateDuplicatedStationName(final String name) {
        stationDao.findByName(name)
            .ifPresent(station -> {
                throw new DuplicatedNameException("중복된 이름의 지하철역입니다.");
            });
    }

    public List<StationResponse> findStations() {
        final List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(it -> new StationResponse(it.getId(), it.getName()))
            .collect(Collectors.toList());
    }

    public Station findById(final Long id) {
        return stationDao.findById(id).
            orElseThrow(() -> new DataNotFoundException("해당 Id의 지하철역이 없습니다."));
    }

    public Station findByName(final String name) {
        return stationDao.findByName(name).
            orElseThrow(() -> new DataNotFoundException("해당 이름의 지하철역이 없습니다."));
    }

    public void deleteStation(final Long id) {
        findById(id);
        stationDao.deleteById(id);
    }
}
