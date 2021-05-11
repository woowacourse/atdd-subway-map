package wooteco.subway.station.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class InMemoryStationDao implements StationDao {
    private final List<Station> stations = new ArrayList<>();
    private Long seq = 0L;

    @Override
    public Station create(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public Station findById(Long id) {
        return stations.stream()
                .filter(station -> station.isSameId(id))
                .findAny()
                .get();
    }

    @Override
    public boolean existById(Long id) {
        return stations.stream()
                .anyMatch(station -> station.isSameId(id));
    }

    @Override
    public boolean existByName(String name) {
        return stations.stream()
                .anyMatch(station -> station.isSameName(name));
    }

    @Override
    public void delete(Long id) {
        stations.removeIf(station -> station.isSameId(id));
    }
}
