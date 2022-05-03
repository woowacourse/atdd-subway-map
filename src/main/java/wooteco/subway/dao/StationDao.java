package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

public class StationDao {
    private static Long seq = 0L;
    private static final List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        checkDuplicateName(station);

        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static void checkDuplicateName(final Station station) {
        boolean isDuplicateName = stations.stream()
                .anyMatch(station::isSameName);
        if (isDuplicateName) {
            throw new IllegalArgumentException("같은 이름을 가진 역이 이미 있습니다");
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

    public static void deleteById(final long id) {
        checkExistId(id);

        stations.removeIf(station -> station.getId() == id);
    }

    private static void checkExistId(final long id) {
        boolean hasSameId = stations.stream()
                .anyMatch(station -> station.getId() == id);
        if (!hasSameId) {
            throw new IllegalArgumentException("존재하지 않는 ID는 삭제할 수 없습니다");
        }
    }
}
