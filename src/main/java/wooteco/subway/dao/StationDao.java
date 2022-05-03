package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

public class StationDao {

    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(final Station station) {
        validateDuplicateName(station.getName());

        final Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static void validateDuplicateName(final String name) {
        final boolean isDuplicate = stations.stream()
                .anyMatch(station -> station.isSameName(name));
        if (isDuplicate) {
            throw new IllegalArgumentException("같은 이름의 역이 이미 존재합니다.");
        }
    }

    private static Station createNewObject(final Station station) {
        final Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static void delete(final Long id) {
        stations.remove(findById(id));
    }

    private static Station findById(final Long id) {
        return stations.stream()
                .filter(station -> station.isSameId(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
    }
}
