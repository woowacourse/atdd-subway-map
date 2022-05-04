package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import wooteco.subway.domain.Station;

@Repository
public class StationDao {
    private Long seq = 0L;
    private Map<Long, Station> stations = new LinkedHashMap<>();

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.put(persistStation.getId(), persistStation);
        return persistStation;
    }

    public boolean existByName(String name) {
        return stations.values()
            .stream()
            .anyMatch(station -> station.getName().equals(name));
    }

    public List<Station> findAll() {
        return new ArrayList<>(stations.values());
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public void delete(Long id) {
        stations.remove(id);
    }
}
