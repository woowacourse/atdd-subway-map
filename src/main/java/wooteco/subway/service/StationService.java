package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.StationNotFoundException;

@Service
public class StationService {

    private static final int NOT_FOUND = 0;

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(final StationRequest request) {
        final Station station = new Station(request.getName());
        final Long id = stationDao.save(station);
        return new StationResponse(id, request.getName());
    }

    public List<StationResponse> showStations() {
        final List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public void deleteStation(final Long id) {
        if (stationDao.delete(id) == NOT_FOUND) {
            throw new StationNotFoundException();
        }
    }
}
