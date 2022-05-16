package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.CreateStationRequest;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.exception.duplicate.DuplicateStationException;
import wooteco.subway.exception.notfound.NotFoundStationException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(final CreateStationRequest request) {
        try {
            final Station station = new Station(request.getName());
            final Long id = stationDao.save(station);
            final Station savedStation = stationDao.findById(id);
            return StationResponse.from(savedStation);
        } catch (final DuplicateKeyException e) {
            throw new DuplicateStationException();
        }
    }

    public List<StationResponse> showStations() {
        final List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public Station show(final Long id) {
        return stationDao.findById(id);
    }

    public void deleteStation(final Long id) {
        validateNotExistStation(id);
        stationDao.delete(id);
    }

    public void validateNotExistStation(final Long id) {
        if (!stationDao.existsById(id)) {
            throw new NotFoundStationException();
        }
    }
}
