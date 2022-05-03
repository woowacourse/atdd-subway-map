package wooteco.subway.dao;

import java.util.Objects;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StationDao {
    private static final String NO_ID_STATION_ERROR_MESSAGE = "해당 아이디의 역이 없습니다.";
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

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
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public static void delete(Long id) {
        Station result = stations.stream()
                .filter(station -> Objects.equals(station.getId(), id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NO_ID_STATION_ERROR_MESSAGE));
        stations.remove(result);
    }

    public static void clear() {
        stations.clear();
        seq = 0L;
    }
}
