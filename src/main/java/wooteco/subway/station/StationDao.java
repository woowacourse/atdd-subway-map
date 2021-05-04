package wooteco.subway.station;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.exception.DuplicatedNameException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        if(validateDuplicateName(station)) {
           throw new DuplicatedNameException();
        }
        Station persistStation = createNewObject(station);
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

    public static void delete(Long id) {
        Station findByIdStation = stations.stream()
                .filter(station -> station.getId() == id.intValue())
                .findFirst()
                .get();
        stations.remove(findByIdStation);
    }

    private static boolean validateDuplicateName(Station newStation) {
        return stations.stream()
                .anyMatch(station -> station.equalName(newStation));
    }
}
