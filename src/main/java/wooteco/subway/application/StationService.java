package wooteco.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DuplicateStationNameException;

@Service
@Transactional
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        if (stationDao.existByName(stationRequest.getName())) {
            throw new DuplicateStationNameException();
        }
        return StationResponse.from(stationDao.save(new Station(stationRequest.getName())));
    }

    public List<StationResponse> findAllStations() {
        return stationDao.findAll().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteStation(Long id) {
        stationDao.deleteById(id);
    }
}
