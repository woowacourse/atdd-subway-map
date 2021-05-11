package wooteco.subway.station.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.station.domain.Station;

@Repository
public class LocalStationDao implements StationDao {
    private final List<Station> stations = new ArrayList<>();
    private Long seq = 0L;

    @Override
    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    @Override
    public Optional<Station> findById(Long stationId) {
        return stations.stream()
            .filter(station -> station.getId().equals(stationId))
            .findAny();
    }

    @Override
    public Optional<Station> findByName(String stationName) {
        return stations.stream()
            .filter(station -> station.getName().equals(stationName))
            .findAny();
    }

    @Override
    public List<Station> findAllByIds(Set<Long> stationIds) {
        return null;
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    @Override
    public void delete(Long id) {
        stations.removeIf(station -> station.getId().equals(id));
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
