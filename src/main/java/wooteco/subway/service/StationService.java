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

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        Station newStation = new Station(stationRequest.getName());
        validateName(newStation);

        Long stationId = stationDao.save(newStation);
        return createStationResponse(stationDao.findById(stationId));
    }

    private void validateName(Station station) {
        if (stationDao.existByName(station)) {
            throw new IllegalArgumentException("이미 존재하는 역 이름입니다.");
        }
    }

    public List<StationResponse> findAll() {
        return stationDao.findAll().stream()
                .map(it -> createStationResponse(it))
                .collect(Collectors.toList());
    }

    public void delete(Long stationId) {
        stationDao.deleteById(stationId);
    }

    private StationResponse createStationResponse(Station newStation) {
        return new StationResponse(newStation.getId(), newStation.getName());
    }
}
