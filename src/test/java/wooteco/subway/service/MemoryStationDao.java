package wooteco.subway.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class MemoryStationDao implements StationDao {
    private static Long seq = 0L;
    private static Map<Long, Station> stations = new HashMap<>();

    @Override
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

    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stations.values());
    }

    @Override
    public int delete(Long id) {
        stations.remove(id);
        return 1;
    }

    @Override
    public boolean existByName(String name) {
        return stations.values().stream()
                .anyMatch(station -> station.isSameName(name));
    }
}
