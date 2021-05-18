package wooteco.subway.station;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.station.StationDuplicatedException;
import wooteco.subway.exception.station.StationNotFoundException;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.web.StationRequest;
import wooteco.subway.station.web.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationDao stationDao;

    public Station findById(Long id) {
        validateExistStation(id);

        return stationDao.findById(id);
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();

        List<StationResponse> stationResponses = stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());

        return stationResponses;
    }

    @Transactional
    public StationResponse save(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        validateExistName(station);

        Station createdStation = stationDao.create(station);
        return StationResponse.create(createdStation);
    }

    @Transactional
    public void removeById(Long id) {
        stationDao.removeById(id);
    }

    public void validateExistStation(Long id) {
        if (!stationDao.existById(id)) {
            throw new StationNotFoundException();
        }
    }

    private void validateExistName(Station station) {
        if (stationDao.existByName(station.getName())) {
            throw new StationDuplicatedException();
        }
    }
}
