package wooteco.subway.service.station;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.request.StationRequest;
import wooteco.subway.controller.dto.response.StationResponse;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.station.Station;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        if (stationDao.existsByName(stationRequest.getName())) {
            throw new IllegalArgumentException("이미 존재하는 지하철 역 이름입니다.");
        }

        Station newStation = stationDao.save(stationRequest.toDomain());
        return StationResponse.of(newStation);
    }

    public List<StationResponse> showStations() {
        List<Station> stations = stationDao.findAll();
        return stations
            .stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
