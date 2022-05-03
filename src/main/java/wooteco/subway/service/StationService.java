package wooteco.subway.service;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class StationService {
    private static final String ALREADY_IN_STATION_ERROR_MESSAGE = "이미 해당 이름의 역이 있습니다.";

    public Station save(Station station) {
        if (isExistStationName(station)) {
            throw new IllegalArgumentException(ALREADY_IN_STATION_ERROR_MESSAGE);
        }
        return StationDao.save(station);
    }

    private boolean isExistStationName(Station station) {
        return StationDao.findAll()
                .stream()
                .anyMatch(inStation -> inStation.isSameName(station));
    }
}
