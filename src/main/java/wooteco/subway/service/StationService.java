package wooteco.subway.service;

import java.util.Optional;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.entity.StationEntity;

public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station register(final String name) {
        validateDuplicateName(name);
        final Station station = new Station(name);
        final StationEntity entity = stationDao.save(station);
        return new Station(entity.getId(), entity.getName());
    }

    private void validateDuplicateName(final String name) {
        final Optional<StationEntity> stationEntity = stationDao.findByName(name);

        if(stationEntity.isPresent()) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 역이름입니다.");
        }
    }
}
