package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DataNotFoundException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        validateNameDuplication(stationRequest);
        Station station = new Station(stationRequest.getName());
        Station newStation = stationDao.save(station);
        return StationResponse.of(newStation);
    }

    private void validateNameDuplication(StationRequest stationRequest) {
        if (stationDao.existByName(stationRequest.getName())) {
            throw new IllegalArgumentException("중복된 지하철 역 이름입니다.");
        }
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        validateExistence(id);
        stationDao.delete(id);
    }

    private void validateExistence(Long id) {
        if (!stationDao.existById(id)) {
            throw new DataNotFoundException("존재하지 않는 지하철 역입니다.");
        }
    }
}
