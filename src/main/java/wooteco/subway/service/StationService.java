package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.station.DuplicatedStationNameException;
import wooteco.subway.exception.station.InvalidStationIdException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        validateStationName(station);
        Station savedStation = stationDao.save(station);
        return new StationResponse(savedStation);
    }

    private void validateStationName(final Station station) {
        if (stationDao.exists(station)) {
            throw new DuplicatedStationNameException();
        }
    }

    public void deleteById(Long id) {
        if (!stationDao.exists(id)) {
            throw new InvalidStationIdException();
        }
        stationDao.deleteById(id);
    }

    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toUnmodifiableList());
    }
}
