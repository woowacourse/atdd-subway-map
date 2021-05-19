package wooteco.subway.dao.station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.station.Station;

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

    @Override
    public boolean existsByName(String name) {
        return stations
            .stream()
            .anyMatch(station -> station.getName().equals(name));
    }

    public void deleteById(Long id) {
        stations.removeIf(station -> station.getId().equals(id));
    }

    @Override
    public Optional<Station> findById(Long id) {
        return stations
            .stream()
            .filter(station -> station.getId().equals(id))
            .findFirst();
    }
}
