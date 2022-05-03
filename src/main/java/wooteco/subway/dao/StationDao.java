package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        validateNotDuplicated(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static void validateNotDuplicated(Station station) {
        if (stations.stream()
                .anyMatch(persistStation -> persistStation.getName().equals(station.getName()))) {
            throw new DuplicateKeyException("이미 존재하는 역입니다.");
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

    public static void deleteAll() {
        stations.clear();
    }
}
