package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.station.StationResponse;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(String name) {
        boolean hasName = stationDao.findAll().stream()
                .anyMatch(it -> it.getName().equals(name));
        if (hasName) {
            throw new IllegalArgumentException("[ERROR] 중복된 이름이 존재합니다.");
        }
        Station newStation = stationDao.save(new Station(name));
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public void deleteStation(Long id) {
        stationDao.findAll().stream()
                .filter(it -> it.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 역 입니다."));

        stationDao.deleteById(id);
    }

    public List<StationResponse> findAll() {
        return stationDao.findAll().stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }
}
