package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;

public class StationService {

    public static StationResponse createStation(String name) {
        if (StationDao.findByName(name)) {
            throw new IllegalArgumentException("같은 이름의 역이 존재합니다.");
        }
        Station newStation = StationDao.save(new Station(name));
        return new StationResponse(newStation);
    }

    public static List<StationResponse> showStations() {
        List<Station> stations = StationDao.findAll();
        return stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    public static void deleteById(Long id) {
        Station station = StationDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 역이 존재하지 않습니다."));
        StationDao.delete(station);
    }
}
