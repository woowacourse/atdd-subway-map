package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public static void deleteStation(long id) {
        stations = stations.stream()
                .filter(station -> station.getId() != id)
                .collect(Collectors.toList());
    }
}
