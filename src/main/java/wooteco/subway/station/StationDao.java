package wooteco.subway.station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ReflectionUtils;

public class StationDao {
    private static Long seq = 0L;
    private static final List<Station> STATIONS = new ArrayList<>();

    public static Station save(Station station) {
        if (isDuplicateStationName(station)) {
            throw new IllegalArgumentException("이미 저장된 역 이름입니다.");
        }

        Station persistStation = createNewObject(station);
        STATIONS.add(persistStation);
        return persistStation;
    }

    private static boolean isDuplicateStationName(Station station) {
        final String stationName = station.getName();
        return STATIONS.stream()
                       .anyMatch(storedStation -> storedStation.getName().equals(stationName));
    }

    public static List<Station> findAll() {
        return STATIONS;
    }

    public static void delete(Long id) {
        STATIONS.removeIf(station -> station.getId().equals(id));
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
