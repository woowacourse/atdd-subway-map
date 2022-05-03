package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        Station persistStation = createNewObject(station);
        if (checkDuplicateStation(persistStation)) {
            throw new IllegalStateException("중복된 지하철역을 저장할 수 없습니다.");
        }
        stations.add(persistStation);
        return persistStation;
    }

    private static boolean checkDuplicateStation(Station station) {
        return stations.stream()
                .anyMatch(s -> s.checkName(station));
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static void deleteAll() {
        stations.clear();
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
