package wooteco.subway.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ReflectionUtils;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class FakeStationDao implements StationDao {
    private Long seq = 0L;
    private Map<Long, Station> stations = new LinkedHashMap<>();

    @Override
    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.put(persistStation.getId(), persistStation);
        return persistStation;
    }

    @Override
    public Station getStation(Long id) {
        return stations.get(id);
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public boolean existByName(String name) {
        return stations.values()
            .stream()
            .anyMatch(station -> station.getName().equals(name));
    }

    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stations.values());
    }

    @Override
    public void delete(Long id) {
        stations.remove(id);
    }

    @Override
    public boolean existById(Long id) {
        return stations.containsKey(id);
    }

}
