package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

public class StationDao {

    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public static List<Station> findAll() {
        return Collections.unmodifiableList(stations);
    }

    public static Optional<Station> findByName(String name) {
        return stations.stream()
                .filter(station -> station.getName().equals(name))
                .findFirst();
    }

    public static Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findFirst();
    }

    public static void deleteAll() {
        stations = new ArrayList<>();
        seq = 0L;
    }

    public static void delete(Station station) {
        stations.remove(station);
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
