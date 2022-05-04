package wooteco.subway.service;


import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

public class StationService {

    public StationResponse save(StationRequest stationRequest) {
        String stationName = stationRequest.getName();

        validateRequest(stationName);

        Station newStation = StationDao.save(new Station(stationName));
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    private void validateRequest(String stationName) {
        List<Station> stations = StationDao.findAll();

        for (Station station : stations) {
            validateName(stationName, station);
        }
    }

    private void validateName(String stationName, Station station) {
        if (station.isSameName(stationName)) {
            throw new IllegalArgumentException("이미 존재하는 역 이름입니다.");
        }
    }

    public List<StationResponse> findAll() {
        return StationDao.findAll().stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public void delete(Long stationId) {
        if (StationDao.delete(stationId)) {
            return;
        }
        throw new IllegalArgumentException("존재하지 않는 역입니다.");
    }
}
