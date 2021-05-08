package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.controller.response.StationResponse;
import wooteco.subway.service.dto.StationDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationDto create(String name) {
        return new StationDto(stationDao.insert(name));
    }

    public List<StationDto> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationDto::new)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
