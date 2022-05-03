package wooteco.subway.repository.dao;

import java.util.Optional;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import wooteco.subway.repository.entity.StationEntity;

public class StationDao {

    private static Long seq = 0L;
    private static List<StationEntity> store = new ArrayList<>();

    public StationEntity save(final StationEntity stationEntity) {
        final StationEntity saved = new StationEntity(++seq, stationEntity.getName());
        store.add(saved);
        return saved;
    }

    public List<StationEntity> findAll() {
        return new ArrayList<>(store);
    }

    public static void removeAll() {
        store.clear();
    }

    public Optional<StationEntity> findByName(final String name) {
        return store.stream()
                .filter(station -> station.getName().equals(name))
                .findAny();
    }
}
