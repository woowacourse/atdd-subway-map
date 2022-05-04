package wooteco.subway.service;


import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

public class StationService {

    public StationResponse save(StationRequest stationRequest) {
        List<Station> stations = StationDao.findAll();
        String stationName = stationRequest.getName();

        for (Station station : stations) {
            if (station.isSameName(stationName)) {
                throw new IllegalArgumentException("이미 존재하는 역 이름입니다.");
            }
        }

        Station newStation = StationDao.save(new Station(stationName));
        return new StationResponse(newStation.getId(), newStation.getName());
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
