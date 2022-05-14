package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DuplicateKeyException;
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

    public StationResponse create(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station savedStation;
        try {
            savedStation = stationDao.save(station);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("이미 존재하는 지하철역 이름입니다.");
        }
        return new StationResponse(savedStation);
    }

    public List<StationResponse> show() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationDao.deleteById(id);
    }
}
