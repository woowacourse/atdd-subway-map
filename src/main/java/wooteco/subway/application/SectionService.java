package wooteco.subway.application;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.constant.SectionNotRegisterException;

import java.util.List;
import java.util.function.Predicate;

public class SectionService {

    private SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Long createSection(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        Section sectionWithSameUpStation = getSectionWithSameUpStation(sections, upStationId);
        Section sectionWithSameDownStation = getSectionWithSameDownStation(sections, downStationId);

        if (conditionForInsertStationInMiddle(distance, sectionWithSameUpStation)) {
            return insertDownStationInMiddle(lineId, upStationId, downStationId, distance, sectionWithSameUpStation);
        }

        if (conditionForInsertStationInMiddle(distance, sectionWithSameDownStation)) {
            return insertUpStationInMiddle(lineId, upStationId, downStationId, distance, sectionWithSameDownStation);
        }

        return saveSection(lineId, upStationId, downStationId, distance);
    }

    private boolean conditionForInsertStationInMiddle(Integer distance, Section section) {
        if (section == null) {
            return false;
        }
        if (distance >= section.getDistance()) {
            throw new SectionNotRegisterException();
        }
        return true;
    }

    private long insertDownStationInMiddle(Long lineId, Long upStationId, Long downStationId, Integer distance, Section existingSection) {
        long createdSectionId = saveSection(lineId, upStationId, downStationId, distance);

        existingSection.setUpStationId(downStationId);
        existingSection.setDistance(existingSection.getDistance() - distance);
        sectionDao.update(existingSection);

        return createdSectionId;
    }

    private Long insertUpStationInMiddle(Long lineId, Long upStationId, Long downStationId, Integer distance, Section existingSection) {
        long createdSectionId = saveSection(lineId, upStationId, downStationId, distance);

        existingSection.setDownStationId(upStationId);
        existingSection.setDistance(existingSection.getDistance() - distance);
        sectionDao.update(existingSection);

        return createdSectionId;
    }

    private Section getSectionWithCondition(List<Section> sections, Predicate<Section> condition) {
        return sections.stream()
                .filter(condition)
                .findAny()
                .orElse(null);
    }

    private Section getSectionWithSameUpStation(List<Section> sections, Long upStationId) {
        return getSectionWithCondition(sections, section -> section.getUpStation().isSameId(upStationId));
    }

    private Section getSectionWithSameDownStation(List<Section> sections, Long downStationId) {
        return getSectionWithCondition(sections, section -> section.getDownStation().isSameId(downStationId));
    }

    private long saveSection(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        Section sectionToSave = Section.builder()
                .lineId(lineId)
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
        return sectionDao.save(sectionToSave);
    }
}
