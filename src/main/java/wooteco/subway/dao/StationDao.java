package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        Station persistStation = createNewObject(station);
        if (hasDuplicateStation(persistStation)) {
            throw new IllegalArgumentException("같은 이름의 역이 존재합니다.");
        }
        stations.add(persistStation);
        return persistStation;
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

    private static boolean hasDuplicateStation(Station persistStation) {
        return stations.stream()
                .anyMatch(persistStation::isSameName);
    }

    public static void deleteAll() {
        stations.clear();
    }
}
