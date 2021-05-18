package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(it -> new StationResponse(it.getId(), it.getName()))
            .collect(Collectors.toList());
    }

    @Transactional
    public StationResponse create(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(),
            newStation.getName());
    }

    @Transactional
    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }

    public Station findById(Long id) {
        Station station = stationDao.findById(id);
        return new Station(station.getId(), station.getName());
    }

    public List<StationResponse> findAllByIds(List<Long> sortedStationIds) {
        return stationDao.findAllByIds(sortedStationIds)
            .stream()
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }
}
