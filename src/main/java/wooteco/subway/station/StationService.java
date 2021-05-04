package wooteco.subway.station;

public class StationService {

    public static StationResponse createStation(String name) {
        if (StationDao.findByName(name)) {
            throw new IllegalArgumentException("같은 이름의 역이 존재합니다.");
        }
        Station newStation = StationDao.save(new Station(name));
        return new StationResponse(newStation.getId(), newStation.getName());
    }
}
