package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        validateStationName(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static void validateStationName(Station persistStation) {
        List<String> stationNames = stations.stream()
                .map(Station::getName)
                .collect(Collectors.toList());
        String stationName = persistStation.getName();

        if (stationNames.contains(stationName)) {
            throw new IllegalArgumentException("같은 이름의 역은 등록할 수 없습니다.");
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

    public static void deleteById(Long id) {
        if (!stations.removeIf(it -> it.getId().equals(id))) {
            throw new IllegalArgumentException("존재하지 않는 역입니다.");
        }
    }
}
