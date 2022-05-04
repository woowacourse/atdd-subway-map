package wooteco.subway.service;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.station.StationResponse;

public class StationService {

    public static StationResponse createStation(String name) {
        if (StationDao.existStationByName(name)) {
            throw new IllegalArgumentException("[ERROR] 중복된 이름이 존재합니다.");
        }
        Station station = new Station(name);
        Station newStation = StationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public static void deleteStation(String name) {
        StationDao.deleteByName(name);
    }
}
