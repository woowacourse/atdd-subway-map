package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.NotFoundStationException;

@Service
@Transactional
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(StationRequest station) {
        Station newStation = Station.from(station);
        validateDuplicateName(newStation);
        return StationResponse.from(stationDao.save(newStation));
    }

    public List<StationResponse> getAllStations() {
        return stationDao.findAll().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        validateExist(id);
        stationDao.deleteById(id);
    }

    private void validateDuplicateName(Station station) {
        boolean isExisting = stationDao.findByName(station.getName()).isPresent();

        if (isExisting) {
            throw new DuplicateNameException();
        }
    }

    private void validateExist(Long id) {
        boolean isExisting = stationDao.findById(id).isPresent();

        if (!isExisting) {
            throw new NotFoundStationException();
        }
    }
}
