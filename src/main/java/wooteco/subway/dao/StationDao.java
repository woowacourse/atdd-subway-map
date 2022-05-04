package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(it -> id.equals(it.getId()))
                .findAny();
    }


    public static boolean existByName(String name) {
        return stations.stream()
                .anyMatch(station -> station.isSameName(name));
    }

    public static void deleteAll() {
        stations.clear();
    }

    public static void deleteById(Long id) {
        Optional<Station> station = findById(id);
        stations.remove(station.get());
    }
}
