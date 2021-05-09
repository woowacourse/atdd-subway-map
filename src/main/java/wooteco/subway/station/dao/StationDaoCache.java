package wooteco.subway.station.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.station.Station;

public class StationDaoCache implements StationDao {

    private final List<Station> stations = new ArrayList<>();
    private Long seq = 0L;

    @Override
    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    @Override
    public Optional<Station> findStationByName(String name) {
        return stations.stream()
            .filter(station -> station.isSameName(name))
            .findAny();
    }

    @Override
    public void remove(Long id) {
        stations.removeIf(station -> station.isSameId(id));
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public void removeAll() {
        stations.clear();
    }
}
