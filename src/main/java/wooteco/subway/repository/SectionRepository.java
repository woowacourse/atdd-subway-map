package wooteco.subway.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionSeries;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.util.SimpleReflectionUtils;

@Repository
public class SectionRepository {

    private final StationRepository stationRepository;
    private final SectionDao sectionDao;

    public SectionRepository(StationRepository stationRepository, SectionDao sectionDao) {
        this.stationRepository = stationRepository;
        this.sectionDao = sectionDao;
    }

    public void persist(Long lineId, SectionSeries sectionSeries) {
        final List<Section> sections = sectionSeries.getSections();
        final List<Long> persistedIds = toIds(readAllSections(lineId));

        deleteSections(sections, persistedIds);
        saveOrUpdateSections(lineId, sections, persistedIds);
    }

    private List<Long> toIds(List<Section> sections) {
        return sections.stream()
            .map(Section::getId)
            .collect(Collectors.toList());
    }

    private void deleteSections(List<Section> sections, List<Long> persistedIds) {
        final List<Long> ids = sections.stream()
            .map(Section::getId)
            .collect(Collectors.toList());

        for (Long persistedId : persistedIds) {
            deleteSectionIfRemoved(ids, persistedId);
        }
    }

    private void deleteSectionIfRemoved(List<Long> ids, Long persistedId) {
        if (!ids.contains(persistedId)) {
            delete(persistedId);
        }
    }

    private void saveOrUpdateSections(Long lineId, List<Section> sections, List<Long> persistedIds) {
        for (Section section : sections) {
            saveOrUpdateSectionEach(lineId, persistedIds, section);
        }
    }

    private void saveOrUpdateSectionEach(Long lineId, List<Long> persistedIds, Section section) {
        if (persistedIds.contains(section.getId())) {
            update(section);
            return;
        }
        save(lineId, section);
    }

    public List<Section> readAllSections(Long lineId) {
        final List<SectionEntity> entities = sectionDao.findSectionsByLineId(lineId);
        return entities.stream()
            .map(entity -> new Section(entity.getId(),
                stationRepository.findById(entity.getUpStationId()),
                stationRepository.findById(entity.getDownStationId()),
                new Distance(entity.getDistance()))
            ).collect(Collectors.toList());
    }

    private Section save(Long lineId, Section createSection) {
        final Long id = sectionDao.save(SectionEntity.from(createSection, lineId));
        return SimpleReflectionUtils.injectId(createSection, id);
    }

    private void update(Section section) {
        sectionDao.update(SectionEntity.from(section));
    }

    private void delete(Long id) {
        sectionDao.deleteById(id);
    }
}
