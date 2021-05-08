package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.controller.response.StationResponse;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.exception.station.StationNotExistException;
import wooteco.subway.service.dto.StationDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationDto create(StationRequest stationRequest) {
        final Long id = stationDao.insert(stationRequest.toEntity());
        final Station station = stationDao.findById(id).orElseThrow(StationNotExistException::new);
        return new StationDto(station);
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
