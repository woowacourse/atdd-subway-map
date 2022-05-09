package wooteco.subway.repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import wooteco.subway.domain.station.Station;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.exception.DuplicateStationNameException;

@Repository
public class StationRepository {

    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Long save(Station station) {
        validateNameNotDuplicated(station.getName());
        return stationDao.save(station);
    }

    private void validateNameNotDuplicated(String name) {
        if (stationDao.existsByName(name)) {
            throw new DuplicateStationNameException(name);
        }
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Station findById(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NoSuchElementException("조회하고자 하는 지하철역이 존재하지 않습니다."));
    }

    public void remove(Long stationId) {
        Optional<Station> station = stationDao.findById(stationId);
        if (station.isEmpty()) {
            throw new NoSuchElementException("삭제하고자 하는 지하철역이 존재하지 않습니다.");
        }
        stationDao.remove(stationId);
    }
}
