package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FakeStationDao implements StationDao {

    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

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
    
    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
    
    @Override
    public int deleteStation(long id) {
        int beforeSize = stations.size();
        stations = stations.stream()
                .filter(station -> station.getId() != id)
                .collect(Collectors.toList());

        if (stations.size() < beforeSize) {
            return 1;
        }
        return 0;
    }
}
