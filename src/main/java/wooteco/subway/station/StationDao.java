package wooteco.subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        Station persistStation = createNewObject(station);
        if (isPersist(persistStation)) {
            throw new IllegalArgumentException("이미 존재하는 역입니다.");
        }
        stations.add(station);
        return persistStation;
    }

    private static boolean isPersist(Station persistStation) {
        return stations.stream()
            .anyMatch(
                persistedStation -> persistedStation.getName().equals(persistStation.getName()));
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

    public static void update(Station station) {
        deleteById(station.getId());
        stations.add(station);
    }

    public static void deleteById(Long id) {
        stations.removeIf(station -> station.getId().equals(id));
    }
}
