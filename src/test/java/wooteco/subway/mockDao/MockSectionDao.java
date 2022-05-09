package wooteco.subway.mockDao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
}
