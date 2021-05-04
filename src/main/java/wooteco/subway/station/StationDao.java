package wooteco.subway.station;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.StationDuplicationException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        validateDuplicatedStation(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static void validateDuplicatedStation(Station newStation) {
        if (isDuplicated(newStation)) {
            throw new StationDuplicationException();
        }
    }

    private static boolean isDuplicated(Station newStation) {
        return stations.stream()
                .anyMatch(station -> station.equals(newStation));
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
        stations.stream()
            .filter(station -> station.isSameId(id))
            .findAny()
            .ifPresent(station -> stations.remove(station));
    }
}
