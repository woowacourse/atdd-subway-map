package wooteco.subway.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(StationRequest request) {
        Optional<Station> foundStation = stationDao.findByName(request.getName());
        if (foundStation.isPresent()) {
            throw new IllegalArgumentException("중복된 이름의 역은 저장할 수 없습니다.");
        }

        Station station = new Station(request.getName());
        Station savedStation = stationDao.save(station);
        return new StationResponse(savedStation.getId(), savedStation.getName());
    }
}
