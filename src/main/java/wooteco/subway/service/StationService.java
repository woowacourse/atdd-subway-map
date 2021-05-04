package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.request.StationRequestDto;
import wooteco.subway.controller.dto.response.StationResponseDto;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationJdbcDao;

    public StationService(StationDao stationJdbcDao) {
        this.stationJdbcDao = stationJdbcDao;
    }

    public StationResponseDto createStation(StationRequestDto stationRequest) {
        stationJdbcDao.findByName(stationRequest.getName()).ifPresent(station -> {
            throw new IllegalArgumentException("이미 존재하는 역 이름입니다.");
        });
        Station newStation = stationJdbcDao.save(stationRequest.getName());
        return new StationResponseDto(newStation.getId(), newStation.getName());
    }

    public List<StationResponseDto> showStations() {
        List<Station> stations = stationJdbcDao.findAll();
        return stations.stream()
                .map(it -> new StationResponseDto(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public long deleteStation(Long id) {
        return stationJdbcDao.deleteById(id);
    }
}
