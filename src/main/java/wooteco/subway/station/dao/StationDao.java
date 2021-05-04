package wooteco.subway.station.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.station.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationDao {
    private static Long seq = 0L;
    private static final List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public static Optional<Station> findById(Long stationId) {
        return stations.stream()
                .filter(station -> station.getId().equals(stationId))
                .findAny();
    }

    public static Optional<Station> findByName(String stationName) {
        return stations.stream()
                .filter(station -> station.getName().equals(stationName))
                .findAny();
    }

    public static List<Station> findAll() {
        return stations;
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public static void delete(Long id) {
        stations.removeIf(station -> station.getId().equals(id));
    }

    public static void clear() {
        stations.clear();
    }
}
