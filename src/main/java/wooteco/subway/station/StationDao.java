package wooteco.subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static void clear() {
        stations.clear();
    }

    public static Station save(final Station station) {
        final Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public static void deleteById(final Long id) {
        stations.removeIf(station -> station.isSameId(id));
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static Optional<Station> findById(final Long id) {
        return stations.stream()
            .filter(station -> station.isSameId(id))
            .findAny();
    }

    public static Optional<Station> findByName(final String name) {
        return stations.stream()
            .filter(station -> station.isSameName(name))
            .findAny();
    }

    private static Station createNewObject(final Station station) {
        final Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
