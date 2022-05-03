package wooteco.subway.dao;

import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StationDao {
    public static final String DUPLICATE_STATION_NAME = "[ERROR] 중복된 역 이름이 있습니다.";
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    private StationDao() {}

    public static Station save(Station station) {
        validateDuplicated(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static void validateDuplicated(Station station) {
        if (isDuplicated(station)) {
            throw new IllegalArgumentException(DUPLICATE_STATION_NAME);
        }
    }

    private static boolean isDuplicated(Station station) {
        return stations.stream().map(Station::getName)
                .anyMatch(name -> station.getName().equals(name));
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static void deleteById(Long id) {
        stations.removeIf(it -> it.getId().equals(id));
    }

    public static Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(it -> it.getId() == id)
                .findFirst();
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
