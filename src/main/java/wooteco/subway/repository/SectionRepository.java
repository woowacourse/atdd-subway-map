package wooteco.subway.repository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionSeries;
import wooteco.subway.entity.SectionEntity;

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
        updateSections(lineId, sections, persistedIds);
    }

    private List<Long> toIds(List<Section> sections) {
        return sections.stream()
            .map(Section::getId)
            .collect(Collectors.toList());
    }

    private void updateSections(Long lineId, List<Section> sections, List<Long> persistedIds) {
        for (Section section : sections) {
            if (persistedIds.contains(section.getId())) {
                update(section);
                continue;
            }
            save(lineId, section);
        }
    }

    private void deleteSections(List<Section> sections, List<Long> persistedIds) {
        final List<Long> ids = sections.stream()
            .map(Section::getId)
            .collect(Collectors.toList());

        for (Long persistedId : persistedIds) {
            if (!ids.contains(persistedId)) {
                delete(persistedId);
            }
        }
    }

    public List<Section> readAllSections(Long lineId) {
        final List<SectionEntity> entities = sectionDao.findSectionsByLineId(lineId);
        return entities.stream()
            .map(entity -> toSection(entity.getId(),
                entity.getUpStationId(),
                entity.getDownStationId(),
                entity.getDistance()))
            .collect(Collectors.toList());
    }

    public Section toSection(Long id, Long upStationId, Long downStationId, int distance) {
        return new Section(
            id,
            stationRepository.findById(upStationId),
            stationRepository.findById(downStationId),
            new Distance(distance)
        );
    }

    private Section save(Long lineId, Section createSection) {
        final Long id = sectionDao.save(SectionEntity.from(createSection, lineId));
        return injectID(createSection, id);
    }

    private Section injectID(Section section, Long id) {
        final Field field = ReflectionUtils.findField(Section.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, section, id);
        return section;
    }

    private void update(Section section) {
        sectionDao.update(SectionEntity.from(section));
    }

    private void delete(Long id) {
        sectionDao.deleteById(id);
    }
}
