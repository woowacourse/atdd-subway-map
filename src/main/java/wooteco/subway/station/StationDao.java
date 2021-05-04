package wooteco.subway.station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;

public final class StationDao {

    private static Long seq = 0L;
    private static final List<Station> stations = new ArrayList<>();

    public static Station save(final Station station) {
        Station persistStation = createNewObject(station);
        if (isDuplicatedName(persistStation)) {
            throw new StationException("이미 존재하는 역 이름입니다.");
        }
        stations.add(persistStation);
        return persistStation;
    }

    private static boolean isDuplicatedName(final Station other) {
        return stations.stream()
                .anyMatch(station -> station.sameName(other));
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static void delete(final Long id) {
        final Station station = findById(id);
        stations.remove(station);
    }

    private static Station findById(final Long id) {
        return stations.stream()
                .filter(station -> station.isId(id))
                .findFirst()
                .orElseThrow(() -> new StationException("존재하지 않는 역입니다."));
    }

    private static Station createNewObject(final Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
