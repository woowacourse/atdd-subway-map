package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

public class StationDao {

    private static Long sequence = 0L;
    private static final List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        validateDuplication(station);
        Station persistStation = createUniqueId(station);
        stations.add(persistStation);
        return persistStation;
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static void deleteAll() {
        stations.clear();
    }

    public static void deleteById(Long id) {
        stations.removeIf(station -> station.getId().equals(id));
    }

    private static void validateDuplication(Station station) {
        if (stations.contains(station)) {
            throw new IllegalArgumentException("이미 존재하는 역입니다.");
        }
    }

    private static Station createUniqueId(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++sequence);
        return station;
    }
}
