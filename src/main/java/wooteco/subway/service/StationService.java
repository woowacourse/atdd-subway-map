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
        List<Station> stations = stationDao.findAll();
        List<String> stationNames = stations.stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        if (stationNames.contains(stationRequest.getName())) {
            throw new IllegalArgumentException("같은 이름의 역은 등록할 수 없습니다.");
        }
        Station station = stationDao.save(new Station(stationRequest.getName()));
        return new StationResponse(station.getId(), station.getName());
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
