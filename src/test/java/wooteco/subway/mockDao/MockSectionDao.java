package wooteco.subway.mockDao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.entity.SectionEntity;

public class MockSectionDao implements SectionDao {

    private static Long seq = 0L;
    private static final Map<Long, SectionEntity> store = new ConcurrentHashMap<>();

    public static void removeAll() {
        store.clear();
    }

    @Override
    public SectionEntity save(final SectionEntity sectionEntity) {
        final SectionEntity saved = new SectionEntity(
                seq++,
                sectionEntity.getLineId(),
                sectionEntity.getUpStationId(),
                sectionEntity.getDownStationId(),
                sectionEntity.getDistance()
        );
        store.put(saved.getId(), saved);

        return saved;
    }

    @Override
    public Optional<SectionEntity> findById(final Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<SectionEntity> findByLineId(final Long lineId) {
        return store.values()
                .stream()
                .filter(sectionEntity -> sectionEntity.getLineId().equals(lineId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(final Long id) {
        store.remove(id);
    }
}
