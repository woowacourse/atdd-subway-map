package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.station.StationRequest;
import wooteco.subway.dto.station.StationResponse;
import wooteco.subway.exception.station.DuplicateStationException;

@Service
@Transactional
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(final StationRequest request) {
        final Station station = new Station(request.getName());
        final Station savedStation = stationDao.insert(station)
                .orElseThrow(DuplicateStationException::new);
        return StationResponse.from(savedStation);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public void delete(final Long id) {
        stationDao.deleteById(id);
    }
}
