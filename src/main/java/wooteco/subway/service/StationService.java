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

    private static final String DUPLICATED_NAME_ERROR_MESSAGE = "중복된 이름이 존재합니다.";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        validDuplicatedName(stationRequest);
        Station station = new Station(stationRequest.getName());
        Long id = stationDao.save(station);
        return new StationResponse(id, stationRequest.getName());
    }

    private void validDuplicatedName(StationRequest stationRequest) {
        if (stationDao.countByName(stationRequest.getName()) > 0) {
            throw new IllegalArgumentException(DUPLICATED_NAME_ERROR_MESSAGE);
        }
    }

    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
