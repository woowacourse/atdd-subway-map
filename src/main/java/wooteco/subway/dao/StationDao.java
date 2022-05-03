package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

public class StationDao {
    private Long seq = 0L;
    private final Map<Long, Station> stations;

    public StationDao() {
        stations = new HashMap<>();
    }

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.put(persistStation.getId(), persistStation);
        return persistStation;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public List<Station> findAll() {
        return new ArrayList<>(stations.values());
    }

    public int delete(final Long stationId) {
        stations.remove(stationId);
        return 1;
    }
}
