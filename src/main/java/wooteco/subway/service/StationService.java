package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.CreateStationRequest;
import wooteco.subway.dto.response.StationResponse;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(final CreateStationRequest request) {
        final Station station = new Station(request.getName());
        final Long id = stationDao.save(station);
        final Station savedStation = stationDao.findById(id);
        return StationResponse.from(savedStation);
    }

    public List<StationResponse> showStations() {
        final List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteStation(final Long id) {
        stationDao.findById(id);
        stationDao.delete(id);
    }
}
