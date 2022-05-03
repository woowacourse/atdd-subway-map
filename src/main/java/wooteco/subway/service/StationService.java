package wooteco.subway.service;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DuplicateStationNameException;

import java.util.List;
import java.util.stream.Collectors;

public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(String name) {
        Station station = new Station(name);
        validateDuplicationName(station);
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    private void validateDuplicationName(Station station) {
        List<Station> stations = stationDao.findAll();
        if (stations.contains(station)) {
            throw new DuplicateStationNameException();
        }
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationDao.deleteById(id);
    }
}
