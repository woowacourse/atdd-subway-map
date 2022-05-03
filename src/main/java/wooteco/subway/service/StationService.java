package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationService() {
        this(new StationDao());
    }

    public StationResponse save(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = StationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public List<StationResponse> findAll() {
        List<Station> stations = StationDao.findAll();
        return stations.stream()
            .map(it -> new StationResponse(it.getId(), it.getName()))
            .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
