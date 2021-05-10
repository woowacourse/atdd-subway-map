package wooteco.subway.station.repository;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.station.Station;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryStationDao implements StationRepository {
    private static AtomicLong seq = new AtomicLong();
    private static Map<Long, Station> stations = new ConcurrentHashMap<>();

    @Override
    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.put(persistStation.getId(), persistStation);
        return persistStation;
    }

    @Override
    public List<Station> findAll() {
        return stations.keySet().stream()
                .sorted()
                .map(id -> stations.get(id))
                .collect(Collectors.toList());
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, seq.incrementAndGet());
        return station;
    }

    @Override
    public void deleteById(Long id) {
        stations.remove(id);
    }

    @Override
    public boolean findByName(String name) {
        return stations.values().stream()
                .anyMatch(station -> station.isSameName(name));
    }
}
