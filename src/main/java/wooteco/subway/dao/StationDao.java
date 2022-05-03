package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.NoStationFoundException;
import wooteco.subway.exception.StationDuplicateException;

public class StationDao {
    private static Long seq = 0L;
    private static final List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        validateDuplicatedStation(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static void validateDuplicatedStation(Station station) {
        if (stations.contains(station)) {
            throw new StationDuplicateException();
        }
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public static List<Station> findAll() {
        return new ArrayList<>(stations);
    }

    public static Station findById(Long id) {
        return stations.stream()
                .filter(station -> station.isSameId(id))
                .findAny()
                .orElseThrow(NoStationFoundException::new);
    }

    public static void deleteById(Long id) {
        stations.remove(findById(id));
    }

    public static void deleteAll() {
        stations.removeAll(new ArrayList<>(stations));
    }
}
