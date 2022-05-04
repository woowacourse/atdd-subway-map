package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public long save(Station station) {
        List<String> stationNames = getStationNames();
        validateName(station, stationNames);
        return stationDao.save(station);
    }

    private static List<String> getStationNames() {
        return StationDao.findAll().stream()
                .map(Station::getName)
                .collect(Collectors.toList());
    }

    private static void validateName(Station station, List<String> stationNames) {
        if (stationNames.contains(station.getName())) {
            throw new IllegalArgumentException("지하철역 이름이 중복됩니다.");
        }
    }

    public static void delete(Long id) {
        StationDao.delete(id);
    }
}
