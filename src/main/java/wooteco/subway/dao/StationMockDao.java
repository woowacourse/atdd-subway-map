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
    public int delete(Long id) {
        boolean isRemoving = stations.removeIf(station -> station.getId().equals(id));
        if (!isRemoving) {
            return 0;
        }
        return 1;
    }

    public void clear() {
        stations.clear();
    }
}
