package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

public class StationDao {

    private static Long seq = 0L;
    private static final List<Station> stations = new ArrayList<>();

    public static List<Station> findAll() {
        return stations;
    }

    public static Station save(Station station) {
        validateUniqueName(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public static void deleteById(Long id) {
        boolean removed = stations.removeIf(it -> it.getId().equals(id));
        if (!removed) {
            throw new IllegalArgumentException("해당되는 역은 존재하지 않습니다.");
        }
    }

    public static void clear() {
        seq = 0L;
        stations.clear();
    }

    private static void validateUniqueName(Station station) {
        boolean hasDuplicate = stations.stream()
                .anyMatch(it -> it.getName().equals(station.getName()));
        if (hasDuplicate) {
            throw new IllegalArgumentException("중복되는 이름의 지하철역이 존재합니다.");
        }
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
