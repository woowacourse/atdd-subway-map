package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.exception.StationIllegalArgumentException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(String stationName) {
        Station station = Station.from(stationName);
        if (stationDao.findByName(stationName).isPresent()) {
            throw new StationIllegalArgumentException("같은 이름의 역이 있습니다;");
        }
        return StationResponse.of(stationDao.save(station));
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationDao.findById(id)
                .orElseThrow(() -> new StationIllegalArgumentException("삭제하려는 역이 존재하지 않습니다"));
        stationDao.delete(id);
    }
}
