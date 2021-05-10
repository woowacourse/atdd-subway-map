package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.controller.request.StationRequest;
import wooteco.subway.exception.station.StationDuplicateException;
import wooteco.subway.exception.station.StationNotFoundException;
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
        validate(stationRequest);
        final Long id = stationDao.insert(stationRequest.toEntity());
        final Station station = stationDao.findById(id).orElseThrow(StationNotFoundException::new);
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

    private void validate(StationRequest stationRequest) {
        final String name = stationRequest.getName();
        final int counts = stationDao.countsByName(name);
        if (counts > 0) {
            throw new StationDuplicateException();
        }
    }
}
