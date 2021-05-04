package wooteco.subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        Station persistStation = setId(station);
        if (isDuplicatedName(station)) {
            throw new IllegalArgumentException(String.format("역 이름이 중복되었습니다. 중복된 역 이름 : %s", station.getName()));
        }
        stations.add(persistStation);
        return persistStation;
    }

    private static boolean isDuplicatedName(Station station) {
        return stations.stream()
                .anyMatch(station1 -> station1.getName().equals(station.getName()));
    }

    public static List<Station> findAll() {
        return stations;
    }

    private static Station setId(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
