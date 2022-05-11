package wooteco.subway.mockDao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.StationEntity;

public class MockStationDao implements StationDao {

    private static Long seq = 0L;
    private static final List<StationEntity> store = new ArrayList<>();

    public void removeAll() {
        store.clear();
    }

    @Override
    public StationEntity save(final StationEntity stationEntity) {
        final long duplicateNameCount = store.stream()
                .filter(it -> it.getName().equals(stationEntity.getName()))
                .count();
        if (duplicateNameCount != 0) {
            throw new DuplicateKeyException(null);
        }
        final StationEntity saved = new StationEntity(++seq, stationEntity.getName());
        store.add(saved);
        return saved;
    }

    @Override
    public List<StationEntity> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public StationEntity findById(final Long id) {
        return store.stream()
                .filter(stationEntity -> stationEntity.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 역을 찾을 수 없습니다."));
    }

    public Optional<StationEntity> findByName(final String name) {
        return store.stream()
                .filter(stationEntity -> stationEntity.getName().equals(name))
                .findAny();
    }

    @Override
    public void deleteById(final Long id) {
        store.remove(findById(id));
    }
}
