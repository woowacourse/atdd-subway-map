package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class StationService {

    static final String DUPLICATE_EXCEPTION_MESSAGE = "이름이 중복된 역은 만들 수 없습니다.";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse insertStation(StationRequest stationRequest) {
        Station station = stationRequest.toEntity();
        validateDuplicateName(station);
        Station newStation = stationDao.insert(station);
        return new StationResponse(newStation);
    }

    private void validateDuplicateName(Station station) {
        if(stationDao.existByName(station)){
            throw new IllegalArgumentException(DUPLICATE_EXCEPTION_MESSAGE);
        }
    }

    public List<StationResponse> findStations() {
        List<Station> stations = stationDao.findAll();
        List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toUnmodifiableList());
        return stationResponses;
    }

    public void deleteStation(Long id) {
        stationDao.delete(id);
    }
}
