package wooteco.subway.mockDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.dao.DataIntegrityViolationException;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.StationEntity;

public class MockStationDao implements StationDao {

    private static Long seq = 0L;
    private static final Map<Long, StationEntity> store = new ConcurrentHashMap<>();

    public static void removeAll() {
        store.clear();
    }

    public StationEntity save(final StationEntity stationEntity) {
        if (existName(stationEntity.getName())) {
            throw new DataIntegrityViolationException("이름 중복되어서 라인 추가 불가능");
        }
        final StationEntity saved = new StationEntity(++seq, stationEntity.getName());
        store.put(saved.getId(), saved);
        return saved;
    }

    private boolean existName(final String name) {
        final Optional<StationEntity> any = store.values().stream()
                .filter(lineEntity -> lineEntity.getName().equals(name))
                .findAny();

        return any.isPresent();
    }

    public List<StationEntity> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<StationEntity> findByName(final String name) {
        return store.values().stream()
                .filter(stationEntity -> stationEntity.getName().equals(name))
                .findAny();
    }

    public Optional<StationEntity> findById(final Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public void deleteById(final Long id) {
        store.remove(id);
    }
}
