package wooteco.subway.station;

import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    public List<Station> findAll() {
        return stations;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public Optional<Station> findById(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findFirst();
    }

    public void delete(Station station) {
        stations.remove(station);
    }

    public Optional<Station> findByName(String stationName) {
        return stations.stream()
                .filter(station -> station.getName().equals(stationName))
                .findFirst();
    }
}
