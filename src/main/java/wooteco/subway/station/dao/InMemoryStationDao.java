package wooteco.subway.station.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.station.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryStationDao implements StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    @Override
    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    @Override
    public Optional<Station> findByName(String name) {
        return stations.stream()
                .filter(station -> station.isSameName(name))
                .findAny();
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
    public void delete(Long id) {
        stations.stream()
                .filter(station -> station.isSameId(id))
                .findAny()
                .ifPresent(station -> stations.remove(station));
    }
}
