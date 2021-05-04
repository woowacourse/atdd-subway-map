package wooteco.subway.service;

import java.util.Optional;
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
        validateStationNameDuplicate(stationRequest);
        Station newStation = new Station(stationRequest.getName());
        Station savedNewStation = stationDao.save(newStation);
        return new StationResponseDto(savedNewStation);
    }

    private void validateStationNameDuplicate(StationRequestDto stationRequest) {
        Optional<Station> foundStationByName = stationDao.findByName(stationRequest.getName());
        foundStationByName.ifPresent(station -> {
            throw new IllegalArgumentException("이미 존재하는 역 이름입니다.");
        });
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
