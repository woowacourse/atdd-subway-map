package wooteco.subway.station.service;

import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

public class StationService {

    public Station save(String stationName) {
        Station station = new Station(stationName);
        if (StationDao.findByName(stationName).isPresent()) {
            throw new IllegalArgumentException("같은 이름의 역이 있습니다;");
        }
        return StationDao.save(station);
    }

    public List<StationResponse> findAll() {
        List<Station> stations = StationDao.findAll();
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        StationDao.delete(id);
    }
}
