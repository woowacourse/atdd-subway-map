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
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponseDto createStation(StationRequestDto stationRequest) {
        stationDao.findByName(stationRequest.getName()).ifPresent(station -> {
            throw new IllegalArgumentException("이미 존재하는 역 이름입니다.");
        });
        Long createdStationId = stationDao.create(stationRequest.getName());
        Station newStation = new Station(createdStationId, stationRequest.getName());
        return new StationResponseDto(newStation.getId(), newStation.getName());
    }

    public List<StationResponseDto> showStations() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(it -> new StationResponseDto(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public long deleteStation(Long id) {
        return stationDao.deleteById(id);
    }
}
