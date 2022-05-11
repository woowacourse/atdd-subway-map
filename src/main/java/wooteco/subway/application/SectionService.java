package wooteco.subway.application;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

import java.util.List;

public class SectionService {

    private SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Long createSection(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        List<Section> sections = sectionDao.findByLineId(lineId);
        Section sectionWithSameUpStation = sectionWithSameUpStation(upStationId, sections);
        Section sectionWithSameDownStation = sectionWithSameDownStation(downStationId, sections);

        if (conditionForInsertStationInMiddle(distance, sectionWithSameUpStation)) {
            return insertDownStationInMiddle(lineId, upStationId, downStationId, distance, sectionWithSameUpStation);
        }

        if (conditionForInsertStationInMiddle(distance, sectionWithSameDownStation)) {
            return insertUpStationInMiddle(lineId, upStationId, downStationId, distance, sectionWithSameDownStation);
        }

        return saveSection(lineId, upStationId, downStationId, distance);
    }

    private boolean conditionForInsertStationInMiddle(Integer distance, Section section) {
        return section != null && distance < section.getDistance();
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

    private Section sectionWithSameDownStation(Long downStationId, List<Section> sections) {
        return sections.stream()
                .filter(section -> section.getDownStation().isSameId(downStationId))
                .findAny()
                .orElse(null);
    }

    private Section sectionWithSameUpStation(Long upStationId, List<Section> sections) {
        return sections.stream()
                .filter(section -> section.getUpStation().isSameId(upStationId))
                .findAny()
                .orElse(null);
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
