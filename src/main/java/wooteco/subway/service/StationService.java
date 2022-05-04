package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Transactional
@Service
public class StationService {
    private final JdbcStationDao stationDao;

    public StationService(JdbcStationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        final Station station = new Station(stationRequest.getName());
        if (stationDao.hasStation(stationRequest.getName())) {
            throw new IllegalArgumentException("같은 이름의 역이 존재합니다.");
        }
        final Long newStationId = stationDao.save(station);
        return new StationResponse(newStationId, station.getName());
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        final List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toUnmodifiableList());
    }

    public void deleteStation(Long id) {
        checkExistStation(id);
        stationDao.deleteById(id);
    }

    private Station checkExistStation(Long id) {
        final Station station = stationDao.findById(id);
        if (station == null) {
            throw new IllegalArgumentException("해당하는 역이 존재하지 않습니다.");
        }
        return station;
    }
}
