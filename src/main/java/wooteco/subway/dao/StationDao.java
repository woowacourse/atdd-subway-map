package wooteco.subway.dao;

import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StationDao {

    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public List<Station> findAll() {
        return stations;
    }

    public boolean existsByName(String name) {
        return stations.stream()
                .map(Station::getName)
                .filter(it -> it.equals(name))
                .count() != 0;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public void deleteAll() {
        stations = new ArrayList<>();
    }
}
