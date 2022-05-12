package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

public class FakeStationDao implements StationDao {

    private Long seq = 0L;
    private Map<Long, Station> stations = new HashMap<>();

    @Override
    public Station insert(Station station) {
        Station persistStation = createNewObject(station);
        stations.put(station.getId(), persistStation);
        return persistStation;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public Boolean existByName(Station station) {
        return stations.values().stream()
                .anyMatch(it -> it.getName().equals(station.getName()));
    }

    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stations.values());
    }

    @Override
    public Station findById(Long id) {
        return stations.get(id);
    }

    @Override
    public void delete(Long id) {
        stations.remove(id);
    }
}
