package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.request.StationRequest;
import wooteco.subway.controller.dto.response.StationResponse;
import wooteco.subway.dao.StationJdbcDao;
import wooteco.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationJdbcDao stationJdbcDao;

    public StationService(StationJdbcDao stationJdbcDao) {
        this.stationJdbcDao = stationJdbcDao;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        stationJdbcDao.findByName(stationRequest.getName()).ifPresent(station -> {
            throw new IllegalArgumentException("이미 존재하는 역 이름입니다.");
        });
        Station newStation = stationJdbcDao.save(stationRequest.getName());
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public List<StationResponse> showStations() {
        List<Station> stations = stationJdbcDao.findAll();
        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public long deleteStation(Long id) {
        return stationJdbcDao.deleteById(id);
    }
}
