package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.StationEntity;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Station register(final String name) {
        validateDuplicateName(name);
        final Station station = Station.createWithoutId(name);
        final StationEntity savedStationEntity = stationDao.save(new StationEntity(station));
        return new Station(savedStationEntity.getId(), savedStationEntity.getName());
    }

    private void validateDuplicateName(final String name) {
        if (stationDao.findByName(name).isPresent()) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 역 이름입니다.");
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<Station> searchAll() {
        return stationDao.findAll()
                .stream()
                .map(stationEntity -> new Station(stationEntity.getId(), stationEntity.getName()))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void remove(final Long id) {
        stationDao.deleteById(id);
    }
}
