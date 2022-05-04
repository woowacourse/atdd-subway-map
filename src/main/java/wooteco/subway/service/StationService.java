package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(final StationRequest request) {
        final Station station = new Station(request.getName());
        final Long id = stationDao.save(station);
        final Station newStation = stationDao.find(id);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public List<StationResponse> showStations() {
        final List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public void deleteStation(final Long id) {
        stationDao.find(id);
        stationDao.delete(id);
    }
}
