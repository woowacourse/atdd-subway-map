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
        validateDuplicateName(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static void validateDuplicateName(Station station) {
        stations.stream()
                .filter(it -> it.isSameStation(station))
                .findAny()
                .ifPresent(it -> {
                    throw new IllegalArgumentException(String.format("%s은 이미 존재하는 지하철 역입니다.", it.getName()));
                });
    }

    public static List<Station> findAll() {
        return stations;
    }

    public static void deleteById(Long id) {
        Station station = stations.stream()
                .filter(it -> it.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("삭제하려는 지하철 역이 존재하지 않습니다."));

        stations.remove(station);
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
