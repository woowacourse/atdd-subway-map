package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

public class StationMockDao implements StationDao {

    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    @Override
    public long save(final Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation.getId();
    }

    @Override
    public boolean existStationById(final Long id) {
        List<Long> stationNames = stations.stream()
                .map(Station::getId)
                .collect(Collectors.toList());
        return stationNames.contains(id);
    }

    @Override
    public boolean existStationByName(final String name) {
        List<String> stationNames = stations.stream()
                .map(Station::getName)
                .collect(Collectors.toList());
        return stationNames.contains(name);
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    @Override
    public void delete(final Long id) {
        stations.removeIf(station -> station.getId().equals(id));
    }

    public void clear() {
        stations.clear();
    }

    private Station createNewObject(final Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
