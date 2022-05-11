package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.dto.response.StationResponse;

@Transactional
@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        final Station station = new Station(stationRequest.getName());
        if (stationDao.hasStation(stationRequest.getName())) {
            throw new IllegalArgumentException("같은 이름의 역이 존재합니다.");
        }
        final Station savedStation = stationDao.save(station);
        return StationResponse.of(savedStation);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        final List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    public StationResponse findStation(Long id) {
        checkExistStation(id);
        final Station station = stationDao.findById(id);
        return new StationResponse(station.getId(), station.getName());
    }

    public void deleteStation(Long id) {
        checkExistStation(id);
        stationDao.deleteById(id);
    }

    private void checkExistStation(Long id) {
        final Station station = stationDao.findById(id);
        if (station == null) {
            throw new IllegalArgumentException("해당하는 역이 존재하지 않습니다.");
        }
    }
}
