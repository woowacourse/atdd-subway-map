package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static List<Station> findAll() {
        return stations;
    }

    public static Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public static boolean existStationByName(String name) {
        return stations.stream()
                .anyMatch(it -> it.getName().equals(name));
    }

    public static Station findByName(String name) {
        return stations.stream()
                .filter(it -> it.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 지하철 역이 존재하지 않습니다."));
    }

    public static void deleteByName(String name) {
        stations.remove(findByName(name));
    }
}
