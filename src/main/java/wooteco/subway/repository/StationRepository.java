package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.station.Station;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.ExceptionType;
import wooteco.subway.exception.NotFoundException;

@Repository
public class StationRepository {

    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<Station> findAllStations() {
        return stationDao.findAll()
                .stream()
                .map(StationEntity::toDomain)
                .collect(Collectors.toList());
    }

    public Station findExistingStation(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NotFoundException(ExceptionType.STATION_NOT_FOUND))
                .toDomain();
    }

    public boolean checkExistingStationName(String name) {
        return stationDao.findByName(name).isPresent();
    }

    public Station save(Station station) {
        StationEntity savedStation = stationDao.save(new StationEntity(station.getName()));
        return savedStation.toDomain();
    }

    public void delete(Station station) {
        stationDao.deleteById(station.getId());
    }
}
