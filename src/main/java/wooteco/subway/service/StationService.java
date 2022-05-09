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

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        return StationResponse.from(newStation);
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(StationResponse::from)
            .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return stationDao.deleteById(id);
    }
}
