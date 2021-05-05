package wooteco.subway.station.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.station.Station;

@Component
public class CollectionStationDao implements StationDao {

    private static final List<Station> stations = new ArrayList<>();
    private Long seq = 0L;

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        if (isPersist(persistStation)) {
            throw new IllegalArgumentException("이미 존재하는 역입니다.");
        }
        stations.add(station);
        return persistStation;
    }

    private boolean isPersist(Station persistStation) {
        return stations.stream()
            .anyMatch(
                persistedStation -> persistedStation.getName().equals(persistStation.getName()));
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

    public void deleteById(Long id) {
        stations.removeIf(station -> station.getId().equals(id));
    }

    @Override
    public Station findById(Long id) {
        return stations
            .stream()
            .filter(station -> station.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
    }
}
