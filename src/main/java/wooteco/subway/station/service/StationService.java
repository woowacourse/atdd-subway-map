package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(String stationName) {
        Station station = new Station(stationName);
        if (stationDao.findByName(stationName).isPresent()) {
            throw new IllegalArgumentException("같은 이름의 역이 있습니다;");
        }
        return new StationResponse(stationDao.save(station));
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
