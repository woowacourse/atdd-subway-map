package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station createStation(String name) {
        validateExistName(name);
        Station station = stationDao.save(new Station(name));
        return station;
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
                .anyMatch(it -> it.isName(name));
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
