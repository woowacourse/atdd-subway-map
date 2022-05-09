package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.exception.InternalServerException;

@Service
public class StationService {

    private static final int STATIONS_NOT_DELETED = 0;
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(StationRequest request) {
        Station station = new Station(request.getName());

        try {
            Station savedStation = stationDao.save(station);
            return StationResponse.of(savedStation);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(ExceptionMessage.DUPLICATED_STATION_NAME.getContent());
        }
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        Integer deletedStations = stationDao.deleteById(id);

        if (deletedStations == STATIONS_NOT_DELETED) {
            throw new InternalServerException(ExceptionMessage.UNKNOWN_DELETE_STATION_FAIL.getContent());
        }
    }
}
