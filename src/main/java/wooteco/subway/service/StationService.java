package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.request.station.StationCreateRequestDto;
import wooteco.subway.controller.dto.response.station.StationResponseDto;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.BadRequestException;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponseDto createStation(StationCreateRequestDto stationCreateRequestDto) {
        Station newStation = new Station(stationCreateRequestDto.getName());
        Long id = stationDao.save(newStation);
        return new StationResponseDto(id, newStation);
    }

    public List<StationResponseDto> getAllStations() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(StationResponseDto::new)
            .collect(Collectors.toList());
    }

    public long deleteStationById(Long id) {
        return stationDao.deleteById(id);
    }
}
