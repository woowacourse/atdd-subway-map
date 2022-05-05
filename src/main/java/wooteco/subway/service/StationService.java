package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.station.StationResponse;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse createStation(String name) {
        validateExistName(name);
        Station newStation = stationDao.save(new Station(name));
        return new StationResponse(newStation.getId(), newStation.getName());
    }

    public void deleteStation(Long id) {
        validateNonFoundId(id);
        stationDao.deleteById(id);
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    private void validateExistName(String name) {
        boolean hasName = stationDao.findAll().stream()
                .anyMatch(it -> it.getName().equals(name));
        if (hasName) {
            throw new IllegalArgumentException("[ERROR] 중복된 이름이 존재합니다.");
        }
    }

    private void validateNonFoundId(Long id) {
        stationDao.findAll().stream()
                .filter(it -> it.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 역 입니다."));
    }
}
