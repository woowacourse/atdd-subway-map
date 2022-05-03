package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

public class StationDao {
    private static Long seq = 0L;
    private static final List<Station> stations = new ArrayList<>();

    public static Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findFirst();
    }

    public static Optional<Station> findByName(String name) {
        return stations.stream()
                .filter(station -> station.getName().equals(name))
                .findAny();
    }

    public static Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public static List<Station> findAll() {
        return stations;
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        if (field != null) {
            field.setAccessible(true);
            ReflectionUtils.setField(field, station, ++seq);
        }
        return station;
    }

    public static void deleteAll() {
        stations.clear();
    }

    public static void deleteById(Long id) {
        Optional<Station> wrappedStation = findById(id);
        if (wrappedStation.isEmpty()) {
            throw new IllegalArgumentException("해당 지하철역이 존재하지 않습니다.");
        }
        stations.remove(wrappedStation.get());
    }
}
