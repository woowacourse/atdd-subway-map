package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationDao {
    private static final String ERROR_MESSAGE_NOT_FOUND_LINE_ID = "Id에 해당하는 노선이 없습니다.";
    private static final long AFFECTED_ROWS_COUNT = 1;
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        validateDuplicate(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static void validateDuplicate(Station station) {
        boolean isDuplicate = stations.stream()
                .anyMatch(s -> s.getName().equals(station.getName()));
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
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

    public static Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findFirst();
    }

    public static Long deleteById(Long id) {
        Station foundStation = findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_FOUND_LINE_ID));
        stations.remove(foundStation);
        return AFFECTED_ROWS_COUNT;
    }

    public static void deleteAll() {
        stations.clear();
    }
}
