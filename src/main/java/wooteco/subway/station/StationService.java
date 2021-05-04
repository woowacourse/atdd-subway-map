package wooteco.subway.station;

import java.util.NoSuchElementException;

public class StationService {

    public void createStation(final String name) {
        final Station station = new Station(name);
        StationDao.save(station);
    }

    public Station findByName(final String name) {
        return StationDao.findByName(name).
            orElseThrow(() -> new NoSuchElementException("해당 이름의 지하철역이 없습니다."));
    }
}
