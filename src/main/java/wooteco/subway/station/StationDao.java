package wooteco.subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        validateStation(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static void validateStation(Station newStation) {
        if (duplicatedNameExists(newStation.getName())) {
            throw new IllegalArgumentException("중복된 지하철 역입니다.");
        }
    }

    private static boolean duplicatedNameExists(String newStation) {
        return stations.stream()
                .anyMatch(station -> station.getName().equals(newStation));
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
}
