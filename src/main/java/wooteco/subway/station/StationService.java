package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import wooteco.subway.line.section.Sections;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation);
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(StationResponse::new)
            .collect(Collectors.toList());
    }

    public void deleteById(Long stationId) {
        stationDao.delete(stationId);
    }

    public List<StationResponse> findByLineId(Long lineId, Sections sections) {
        Stations stations = new Stations(stationDao.findByLineId(lineId));
        stations.sort(sections);
        return stations.toResponse();
    }
}
