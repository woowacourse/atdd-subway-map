package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.property.Distance;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.SectionSeries;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.util.SimpleReflectionUtils;

@Repository
public class SectionRepository {

    private final PersistManager<SectionEntity> persistManager;
    private final StationRepository stationRepository;
    private final SectionDao sectionDao;

    public SectionRepository(PersistManager<SectionEntity> persistManager,
        StationRepository stationRepository, SectionDao sectionDao) {
        this.persistManager = persistManager;
        this.stationRepository = stationRepository;
        this.sectionDao = sectionDao;
    }

    public void persist(Long lineId, SectionSeries sectionSeries) {
        final List<Long> persistedIds = toIds(findAllSections(lineId));
        final List<Section> sections = sectionSeries.getSections();
        for (Section section : sections) {
            final SectionEntity entity = SectionEntity.from(section, lineId);
            final Long id = persistManager.persist(sectionDao, entity, persistedIds);
            persistedIds.remove(id);
            SimpleReflectionUtils.injectId(section, id);
        }
        persistManager.deletePersistedAll(sectionDao, persistedIds);
    }

    private List<Long> toIds(List<Section> sections) {
        return sections.stream()
            .map(Section::getId)
            .collect(Collectors.toList());
    }

    public List<Section> findAllSections(Long lineId) {
        final List<SectionEntity> entities = sectionDao.findSectionsByLineId(lineId);
        return entities.stream()
            .map(entity -> new Section(entity.getId(),
                stationRepository.findById(entity.getUpStationId()),
                stationRepository.findById(entity.getDownStationId()),
                new Distance(entity.getDistance()))
            ).collect(Collectors.toList());
    }

    private void persistEach(Long lineId, List<Long> persistedIds, List<Section> sections) {
        for (Section section : sections) {
            final SectionEntity entity = SectionEntity.from(section, lineId);
            final Long id = persistManager.persist(sectionDao, entity, persistedIds);
            SimpleReflectionUtils.injectId(section, id);
        }
    }
}
