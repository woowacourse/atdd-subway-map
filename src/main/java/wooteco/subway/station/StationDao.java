package wooteco.subway.station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;

public final class StationDao {

    private static Long seq = 0L;
    private static final List<Station> stations = new ArrayList<>();

    public static Station save(final Station station) {
        if (isAlreadyExist(station)) {
            throw new StationException("이미 존재하는 역 이름입니다.");
        }

        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static boolean isAlreadyExist(final Station station) {
        return stations.contains(station);
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static void delete(final Long id){
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
