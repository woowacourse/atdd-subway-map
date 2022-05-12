package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.RowNotFoundException;

@Repository
public class StationRepository {

    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station findById(Long id) {
        final StationEntity entity = stationDao.findById(id)
            .orElseThrow(() -> new RowNotFoundException("삭제하고자 하는 역이 존재하지 않습니다."));
        return new Station(entity.getId(), entity.getName());
    }

    public List<Station> findAllStations() {
        return stationDao.findAll()
            .stream()
            .map(entity -> new Station(entity.getId(), entity.getName()))
            .collect(Collectors.toList());
    }

    public Station create(Station station) {
        final StationEntity saved = stationDao.save(StationEntity.from(station));
        return new Station(saved.getId(), saved.getName());
    }

    public void deleteById(Long id) {
        final boolean isDeleted = stationDao.deleteById(id);
        if (!isDeleted) {
            throw new RowNotFoundException("삭제하고자 하는 역이 존재하지 않습니다.");
        }
    }
}
