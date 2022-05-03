package wooteco.subway.application;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateNameException;

import static org.springframework.util.StringUtils.hasText;

public class StationService {
    public StationService() {
    }

    public Station saveByName(String name) {
        if (StationDao.existByName(name)) {
            throw new DuplicateNameException();
        }
        return StationDao.save(new Station(name));
    }
}