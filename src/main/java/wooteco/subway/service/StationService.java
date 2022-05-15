package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.station.StationResponseDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponseDto createStation(String name) {
        validateExistName(name);
        Station station = stationDao.create(new Station(name));

        return new StationResponseDto(station);
    }

    private void validateExistName(String name) {
        if (stationDao.existByName(name)) {
            throw new IllegalArgumentException("[ERROR] 중복된 이름이 존재합니다.");
        }
    }

    public List<StationResponseDto> findStations() {
        return stationDao.findAll().stream()
                .map(StationResponseDto::new)
                .collect(Collectors.toList());
    }

    public Station findById(Long id) {
        return stationDao.findById(id);
    }

    public void delete(Long id) {
        validateNonFoundId(id);

        stationDao.deleteById(id);
    }

    private void validateNonFoundId(Long id) {
        if (!stationDao.existById(id)) {
            throw new NoSuchElementException("[ERROR] 존재하지 않는 역 입니다.");
        }
    }
}
