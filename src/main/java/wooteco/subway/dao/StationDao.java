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
        validateDuplicate(station.getName());
        stations.add(persistStation);
        return persistStation;
    }

    private static void validateDuplicate(String stationName) {
        boolean isDuplicate = stations.stream()
                .anyMatch(station -> station.isSameName(stationName));
        if (isDuplicate) {
            throw new IllegalArgumentException("이름이 중복된 역은 만들 수 없습니다.");
        }
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

    public static void delete(Long id) {
        Station foundStation = stations.stream()
                .filter(station -> station.isSameId(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 역은 존재하지 않습니다."));
        stations.remove(foundStation);
    }

    public static void clear() {
        stations.clear();
    }
}
