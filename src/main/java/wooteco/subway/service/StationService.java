package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.StationEntity;

public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station register(final String name) {
        validateDuplicateName(name);
        final Station station = new Station(name);
        final StationEntity savedEntity = stationDao.save(new StationEntity(station));
        return new Station(savedEntity.getId(), savedEntity.getName());
    }

    private void validateDuplicateName(final String name) {
        final Optional<StationEntity> stationEntity = stationDao.findByName(name);
        if (stationEntity.isPresent()) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 역 이름입니다.");
        }
    }

    public List<Station> searchAll() {
        return stationDao.findAll()
                .stream()
                .map(stationEntity -> new Station(stationEntity.getId(), stationEntity.getName()))
                .collect(Collectors.toList());
    }

    public void remove(final Long id) {
        stationDao.deleteById(id);
    }
}
