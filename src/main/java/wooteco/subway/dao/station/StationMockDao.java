package wooteco.subway.dao.station;

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
    public long save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation.getId();
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public boolean existStationById(Long id) {
        List<Long> stationIds = stations.stream()
                .map(Station::getId)
                .collect(Collectors.toList());
        return stationIds.contains(id);
    }

    @Override
    public boolean existStationByName(String name) {
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
    public void delete(Long id) {
        stations.removeIf(station -> station.getId().equals(id));
    }

    public void clear() {
        stations.clear();
    }
}
