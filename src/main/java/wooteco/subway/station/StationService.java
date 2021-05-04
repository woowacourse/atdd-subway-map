package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;

public class StationService {

    public StationResponse save(StationRequest stationRequest) {
        validateStationName(stationRequest);
        Station station = new Station(stationRequest.getName());
        Station newStation = StationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    private void validateStationName(StationRequest stationRequest) {
        if (checkNameDuplicate(stationRequest)) {
            throw new IllegalArgumentException("중복된 이름의 역이 존재합니다.");
        }
    }

    private boolean checkNameDuplicate(StationRequest stationRequest) {
        return StationDao.findAll().stream()
                .anyMatch(station -> station.isSameName(stationRequest.getName()));
    }

    public List<StationResponse> findAllStations() {
        List<Station> stations = StationDao.findAll();
        return stations.stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public void deleteStation(Long id) {
        StationDao.deleteById(id);
    }
}
