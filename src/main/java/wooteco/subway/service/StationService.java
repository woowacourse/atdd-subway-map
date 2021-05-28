package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.StationDuplicationException;
import wooteco.subway.exception.StationNotFoundException;
import wooteco.subway.repository.StationDao;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(StationRequest stationRequest) {
        Station station = stationRequest.toStation();
        validateDuplication(station.getName());

        long id = stationDao.save(station);
        station.setId(id);
        return StationResponse.of(station);
    }

    public StationResponse findById(long id) {
        Station station = stationDao.findById(id)
            .orElseThrow(StationNotFoundException::new);
        return StationResponse.of(station);
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(StationResponse::of)
            .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }

    private void validateDuplication(String name) {
        boolean isDuplicated = stationDao.findAll()
            .stream()
            .anyMatch(station -> station.getName().equals(name));
        if (isDuplicated) {
            throw new StationDuplicationException();
        }
    }
}
