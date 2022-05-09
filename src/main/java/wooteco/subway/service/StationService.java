package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.station.Station;
import wooteco.subway.service.dto.ServiceDtoAssembler;
import wooteco.subway.service.dto.station.StationResponseDto;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponseDto create(String name) {
        validateNameNotDuplicated(name);
        Long stationId = stationDao.save(new Station(name));
        Station station = stationDao.findById(stationId);
        return ServiceDtoAssembler.stationResponseDto(station);
    }

    private void validateNameNotDuplicated(String name) {
        if (stationDao.existsByName(name)) {
            throw new IllegalArgumentException("해당 이름의 지하철 역이 이미 존재합니다.");
        }
    }

    public List<StationResponseDto> findAll() {
        return stationDao.findAll()
                .stream()
                .map(ServiceDtoAssembler::stationResponseDto)
                .collect(Collectors.toUnmodifiableList());
    }

    public void remove(Long id) {
        stationDao.remove(id);
    }
}
