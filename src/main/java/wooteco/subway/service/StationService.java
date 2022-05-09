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

    public StationResponse createStation(final StationRequest stationRequest) {
        validateStationName(stationRequest.getName());
        final Station station = stationDao.save(stationRequest.toEntity());
        return StationResponse.from(station);
    }

    private void validateStationName(final String name) {
        if (stationDao.existsByName(name)) {
            throw new IllegalStateException("이미 존재하는 지하철역입니다.");
        }
    }

    public List<StationResponse> showStations() {
        return stationDao.findAll()
            .stream()
            .map(StationResponse::from)
            .collect(Collectors.toList());
    }

    public void deleteStation(final Long id) {
        stationDao.deleteById(id);
    }
}
