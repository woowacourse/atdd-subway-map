package wooteco.subway.service;

import org.springframework.stereotype.Service;
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

    public Station createStation(Station station) {
        validateDuplicateName(station);
        return stationDao.save(station);
    }

    public List<Station> getAllStations() {
        return stationDao.findAll();
    }

    public void delete(Long id) {
        validateExist(id);
        stationDao.deleteById(id);
    }

    private void validateDuplicateName(Station station) {
        List<String> names = stationDao.findAll().stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        if (names.contains(station.getName())) {
            throw new IllegalArgumentException("이미 존재하는 지하철 역입니다.");
        }
    }

    private void validateExist(Long id) {
        List<Long> stationIds = stationDao.findAll().stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        if (!stationIds.contains(id)) {
            throw new IllegalArgumentException("삭제하려는 지하철 역 ID가 존재하지 않습니다.");
        }
    }
}
