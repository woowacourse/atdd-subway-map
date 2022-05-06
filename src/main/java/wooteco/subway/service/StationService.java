package wooteco.subway.service;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class StationService {

    private static final int DELETE_FAIL = 0;

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest request) {
        final Station station = new Station(request.getName());

        final Long savedId = stationDao.save(station);

        return new StationResponse(savedId, station.getName());
    }

    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(toUnmodifiableList());
    }

    public void deleteById(Long id) {
        final int isDeleted = stationDao.deleteById(id);

        if (isDeleted == DELETE_FAIL) {
            throw new IllegalArgumentException("존재하지 않는 지하철 역입니다.");
        }
    }
}
