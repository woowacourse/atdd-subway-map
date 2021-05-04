package wooteco.subway.dao;

import java.util.Optional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import wooteco.subway.domain.station.Station;

public class StationDao {
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

    public static Optional<Station> findById(long id) {
        return stations.stream()
            .filter(station -> station.getId() == id)
            .findAny();
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public static void deleteById(long id) {
        stations.removeIf(station -> station.getId() == id);
    }
}
