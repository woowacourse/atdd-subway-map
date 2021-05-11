package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.station.response.StationResponse;
import wooteco.subway.exception.station.StationDuplicateException;
import wooteco.subway.dao.StationDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(String name) {
        stationDao.findByName(name)
                .ifPresent(station -> {
                    throw new StationDuplicateException(name);
                });
        return new StationResponse(stationDao.insert(name));

    }

    public List<StationResponse> showAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
