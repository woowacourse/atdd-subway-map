package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineJdbcDao;
import wooteco.subway.dao.StationJdbcDao;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.Stations;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class StationService {

    private final StationJdbcDao stationDao;
    private final LineJdbcDao lineDao;

    public StationService(final StationJdbcDao stationDao, LineJdbcDao lineDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public StationResponse save(StationRequest request) {
        Stations stations = stationDao.findAll();
        Station station = new Station(request.getName());
        stations.add(station);

        Station newStation = stationDao.save(station);
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public List<StationResponse> findAll() {
        Stations stations = stationDao.findAll();
        return stations.getStations()
                .stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public int delete(Long id) {
        lineDao.findAll().validateCanDelete(id);
        stationDao.findAll().validateExist(id);

        return stationDao.delete(id);
    }
}
