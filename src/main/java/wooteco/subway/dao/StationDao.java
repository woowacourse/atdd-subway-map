package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ReflectionUtils;

import wooteco.subway.domain.Station;

public class StationDao {
    private static Long seq = 0L;
    private static Map<Long, Station> stations = new LinkedHashMap<>();

    public static Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.put(persistStation.getId(), persistStation);
        return persistStation;
    }

    public static boolean existByName(String name) {
        return stations.values()
            .stream()
            .anyMatch(station -> station.getName().equals(name));
    }

    public static List<Station> findAll() {
        return new ArrayList<>(stations.values());
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public static void delete(Long id) {
        stations.remove(id);
    }

    public static void deleteAll() {
        stations.clear();
    }
}
