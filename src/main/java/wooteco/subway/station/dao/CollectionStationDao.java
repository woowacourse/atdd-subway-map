package wooteco.subway.station.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.station.Station;

@Component
@Primary
public class CollectionStationDao implements StationDao {
    private Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

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
}
