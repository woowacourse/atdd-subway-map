package wooteco.subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(final Station station) {
        if (findByName(station.getName()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 역 입니다.");
        }
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public static List<Station> findAll() {
        return stations;
    }

    private static Station createNewObject(final Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    private static Optional<Station> findById(final Long id) {
        return stations.stream()
                .filter(station -> station.sameId(id))
                .findAny();
    }

    public static Optional<Station> findByName(final String name) {
        return stations.stream()
                .filter(station -> station.sameName(name))
                .findAny();
    }

    public static void clear() {
        stations.clear();
        seq = 0L;
    }

    public static void delete(final Long id) {
        Station findStation = findById(id).orElseThrow(() -> new IllegalArgumentException("없는 역임!"));
        stations.remove(findStation);
    }
}
