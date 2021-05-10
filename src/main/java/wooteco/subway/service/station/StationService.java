package wooteco.subway.service.station;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.controller.dto.request.station.StationCreateRequestDto;
import wooteco.subway.controller.dto.response.station.StationResponseDto;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.HttpException;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponseDto createStation(StationCreateRequestDto stationCreateRequestDto) {
        Station newStation = new Station(stationCreateRequestDto.getName());
        try {
            Long id = stationDao.save(newStation);
            return new StationResponseDto(id, newStation);
        } catch (DuplicateKeyException e) {
            throw new HttpException(BAD_REQUEST, "이미 존재하는 역 이름입니다.");
        }
    }

    @Transactional(readOnly = true)
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
