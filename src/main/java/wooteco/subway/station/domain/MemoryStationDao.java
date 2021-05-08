package wooteco.subway.station.domain;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemoryStationDao implements StationDao {
    private static Long seq = 0L;
    private static final List<Station> stations = new ArrayList<>();

    @Override
    public Station save(final Station station) {
        if (findByName(station.getName()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 역 입니다.");
        }
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    private Station createNewObject(final Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public Optional<Station> findById(final Long id) {
        return stations.stream()
                .filter(station -> station.sameId(id))
                .findAny();
    }

    @Override
    public Optional<Station> findByName(final String name) {
        return stations.stream()
                .filter(station -> station.sameName(name))
                .findAny();
    }

    @Override
    public void clear() {
        stations.clear();
        seq = 0L;
    }

    @Override
    public void delete(final Long id) {
        Station findStation = findById(id).orElseThrow(() -> new IllegalArgumentException("없는 역임!"));
        stations.remove(findStation);
    }
}
