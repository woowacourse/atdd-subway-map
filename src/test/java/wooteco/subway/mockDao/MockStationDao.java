package wooteco.subway.mockDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.StationEntity;

public class MockStationDao implements StationDao {

    private static Long seq = 0L;
    private static final List<StationEntity> store = new ArrayList<>();

    public static void removeAll() {
        store.clear();
    }

    public StationEntity save(final StationEntity stationEntity) {
        long duplicateNameCount = store.stream()
                .filter(it -> it.getName().equals(stationEntity.getName()))
                .count();
        if (duplicateNameCount != 0) {
            throw new DuplicateKeyException(null);
        }
        final StationEntity saved = new StationEntity(++seq, stationEntity.getName());
        store.add(saved);
        return saved;
    }

    public List<StationEntity> findAll() {
        return new ArrayList<>(store);
    }

    public Optional<StationEntity> findByName(final String name) {
        return store.stream()
                .filter(stationEntity -> stationEntity.getName().equals(name))
                .findAny();
    }

    public Optional<StationEntity> findById(final Long id) {
        return store.stream()
                .filter(stationEntity -> stationEntity.getId().equals(id))
                .findAny();
    }

    public void deleteById(final Long id) {
        findById(id).ifPresent(store::remove);
    }
}
