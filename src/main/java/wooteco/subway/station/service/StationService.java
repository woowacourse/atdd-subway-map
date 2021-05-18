package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.Stations;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.repository.StationDao;

import java.util.List;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse save(final String name) {
        Station station = new Station(name);
        validateDuplicateName(station);

        return StationResponse.toDto(stationDao.save(station));
    }

    private void validateDuplicateName(final Station station) {
        Stations stations = stationDao.findAll();
        if (stations.doesNameExist(station.getName())) {
            throw new DuplicateStationNameException();
        }
    }

    public List<StationResponse> findAll() {
        Stations stations = stationDao.findAll();
        return StationResponse.toDtos(stations);
    }

    @Transactional
    public void delete(final Long id) {
        validateId(id);
        stationDao.deleteById(id);
    }

    private void validateId(final Long id) {
        Stations stations = stationDao.findAll();
        if (stations.doesIdNotExist(id)) {
            throw new NoSuchStationException();
        }
    }

    public Station findById(final Long stationId) {
        return stationDao.findById(stationId).orElseThrow(NoSuchStationException::new);
    }
}
