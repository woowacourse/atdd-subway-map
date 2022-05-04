package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import wooteco.subway.domain.Station;

public class FakeStationDao {

    private static Long seq = 0L;
    private static final List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        validateDuplicateName(station);
        Station persistStation = createStation(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static void validateDuplicateName(Station station) {
        if (stations.contains(station)) {
            throw new IllegalArgumentException("중복된 이름이 존재합니다.");
        }
    }

    public static List<Station> findAll() {
        return stations;
    }

    private static Station createStation(Station station) {
        return new Station(++seq, station.getName());
    }

    public static boolean deleteById(Long id) {
        return stations.removeIf(it -> it.getId().equals(id));
    }
}
