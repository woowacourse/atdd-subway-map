package wooteco.subway.application;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.constant.TerminalStation;
import wooteco.subway.exception.constant.SectionNotRegisterException;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

        validateUpAndDownStationAllExist(sectionWithSameUpStation, sectionWithSameDownStation);
        validateUpAndDownStationNotAllExist(sections, sectionWithSameUpStation, sectionWithSameDownStation);

        return saveSection(lineId, upStationId, downStationId, distance);
    }

    private void validateUpAndDownStationAllExist(Section sectionWithSameUpStation, Section sectionWithSameDownStation) {
        if (sectionWithSameUpStation != null && sectionWithSameDownStation != null) {
            throw new SectionNotRegisterException();
        }
    }

    private void validateUpAndDownStationNotAllExist(List<Section> sections, Section sectionWithSameUpStation, Section sectionWithSameDownStation) {
        if (sectionWithSameUpStation == null && sectionWithSameDownStation == null) {
            throw new SectionNotRegisterException();
        }
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
        return getSectionWithCondition(sections, section -> section.getUpStationId().equals(upStationId));
    }

    private Section getSectionWithSameDownStation(List<Section> sections, Long downStationId) {
        return getSectionWithCondition(sections, section -> section.getDownStationId().equals(downStationId));
    }

    private long saveSection(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        return sectionDao.save(new Section(upStationId, downStationId, distance, lineId));
    }

    public LinkedList<Long> findSortedStations(long lineId) {
        List<Section> foundSections = sectionDao.findByLineId(lineId);
        Map<Long, Long> toDownSectionMap = convertListToDownSectionMap(foundSections);
        Map<Long, Long> toUpSectionMap = convertListToUpSectionMap(foundSections);

        LinkedList<Long> result = new LinkedList<>();
        Long pivot = getAnyPivot(foundSections);
        Long iterator = pivot;
        result.add(pivot);

        insertDownSections(toDownSectionMap, result, iterator);

        iterator = pivot;
        insertUpSections(toUpSectionMap, result, iterator);
        return result;
    }

    public Map<TerminalStation, Long> findTerminalStations(long lineId) {
        LinkedList<Long> sortedStations = findSortedStations(lineId);
        Map<TerminalStation, Long> terminalStationMap = new HashMap<>();
        terminalStationMap.put(TerminalStation.UP, sortedStations.getFirst());
        terminalStationMap.put(TerminalStation.DOWN, sortedStations.getLast());
        return Collections.unmodifiableMap(terminalStationMap);
    }

    private Long getAnyPivot(List<Section> foundSections) {
        return foundSections.get(0).getUpStationId();
    }

    private Map<Long, Long> convertListToUpSectionMap(List<Section> foundSections) {
        return foundSections.stream()
                .collect(Collectors.toMap(section -> section.getDownStationId(), section -> section.getUpStationId()));
    }

    private Map<Long, Long> convertListToDownSectionMap(List<Section> foundSections) {
        return foundSections.stream()
                .collect(Collectors.toMap(section -> section.getUpStationId(), section -> section.getDownStationId()));
    }

    private void insertUpSections(Map<Long, Long> toUpSection, LinkedList<Long> result, Long iterator) {
        while (true) {
            Long upSectionId = toUpSection.get(iterator);
            if (upSectionId == null) {
                break;
            }
            result.addFirst(upSectionId);
            iterator = upSectionId;
        }
    }

    private void insertDownSections(Map<Long, Long> toDownSection, LinkedList<Long> result, Long iterator) {
        while (true) {
            Long downSectionId = toDownSection.get(iterator);
            if (downSectionId == null) {
                break;
            }
            result.addLast(downSectionId);
            iterator = downSectionId;
        }
    }
}
