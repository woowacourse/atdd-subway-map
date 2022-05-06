package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateStationNameException;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.StationEntity;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station register(final String name) {
        validateDuplicateName(name);
        final Station station = new Station(name);
        final StationEntity savedStationEntity = stationDao.save(new StationEntity(station));
        return new Station(savedStationEntity.getId(), savedStationEntity.getName());
    }

    private void validateDuplicateName(final String name) {
        if (stationDao.findByName(name).isPresent()) {
            throw new DuplicateStationNameException();
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
