package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

public class StationService {

    public StationResponse save(StationRequest stationRequest) {
        if (StationDao.existByName(stationRequest.getName())) {
            throw new IllegalArgumentException("중복된 지하철 역 이름입니다.");
        }
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
        StationDao.delete(id);
    }
}
