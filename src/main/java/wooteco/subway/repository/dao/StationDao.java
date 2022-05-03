package wooteco.subway.repository.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import wooteco.subway.repository.entity.StationEntity;

public class StationDao {

    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static StationEntity save(final Station station) {
        final Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return new StationEntity(persistStation);
    }

    public static List<Station> findAll() {
        return new ArrayList<>(stations);
    }

    private static Station createNewObject(final Station station) {
        final Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
