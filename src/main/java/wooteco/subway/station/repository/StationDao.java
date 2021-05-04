package wooteco.subway.station.repository;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.station.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StationDao {
    public static final long INITIAL_INDEX = 0L;
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        Station persistStation = createNewObject(station);
        if (stations.contains(station)) {
            throw new IllegalArgumentException("이미 존재하는 지하철 역입니다.");
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

    public static void deleteAll() {
        seq = INITIAL_INDEX;
        stations.clear();
    }
}
