package wooteco.subway.mockDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.entity.SectionEntity;

public class MockSectionDao implements SectionDao {

    private static Long seq = 0L;
    private static final List<SectionEntity> store = new ArrayList<>();

    public void removeAll() {
        store.clear();
    }

    @Override
    public SectionEntity save(final SectionEntity sectionEntity) {
        SectionEntity sectionEntityForSave = new SectionEntity(
                ++seq,
                sectionEntity.getLineId(),
                sectionEntity.getUpStationId(),
                sectionEntity.getDownStationId(),
                sectionEntity.getDistance()
        );
        store.add(sectionEntityForSave);
        return sectionEntityForSave;
    }

    @Override
    public List<SectionEntity> findByLineId(final Long lineId) {
        return store.stream()
                .filter(sectionEntity -> sectionEntity.getLineId().equals(lineId))
                .collect(Collectors.toList());
    }

    @Override
    public List<SectionEntity> findByStationId(final Long stationId) {
        return store.stream()
                .filter(sectionEntity ->
                        sectionEntity.getUpStationId().equals(stationId) ||
                                sectionEntity.getDownStationId().equals(stationId))
                .collect(Collectors.toList());
    }

    @Override
    public void update(final SectionEntity newSectionEntity) {
        final Long id = newSectionEntity.getId();
        final Long lineId = newSectionEntity.getLineId();
        final SectionEntity oldSectionEntity = store.stream()
                .filter(sectionEntity ->
                        sectionEntity.getId().equals(id) && sectionEntity.getLineId().equals(lineId))
                .findFirst().get();
        store.remove(oldSectionEntity);
        SectionEntity sectionEntityForSave = new SectionEntity(
                oldSectionEntity.getId(),
                oldSectionEntity.getLineId(),
                newSectionEntity.getUpStationId(),
                newSectionEntity.getDownStationId(),
                newSectionEntity.getDistance()
        );
        store.add(sectionEntityForSave);
    }

    @Override
    public void deleteByLineIdAndStationId(final Long lineId, final Long stationId) {
        store.stream()
                .forEach(sectionEntity -> {
                    if (sectionEntity.getLineId().equals(lineId) &&
                            (sectionEntity.getUpStationId().equals(stationId) ||
                                    sectionEntity.getDownStationId().equals(stationId))) {
                        store.remove(sectionEntity);
                    }
                });
    }
}
