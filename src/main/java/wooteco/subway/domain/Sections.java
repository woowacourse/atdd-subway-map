package wooteco.subway.domain;

import lombok.AllArgsConstructor;
import wooteco.subway.domain.constant.TerminalStation;
import wooteco.subway.exception.constant.SectionNotDeleteException;
import wooteco.subway.exception.constant.SectionNotRegisterException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Sections {
    private static final int ONLY_ONE_SECTION = 1;

    private final List<Section> sections;

    public LinkedList<Long> getSorted() {
        Map<Long, Long> toDownSectionMap = getToDownSectionMap(sections);
        Map<Long, Long> toUpSectionMap = getToUpSectionMap(sections);

        LinkedList<Long> result = new LinkedList<>();
        Long pivot = getAnyPivot();
        result.add(pivot);
        addDownSections(toDownSectionMap, result, pivot);
        addUpSections(toUpSectionMap, result, pivot);
        return result;
    }

    public void validateCanDeleteSection() {
        if (sections.size() == ONLY_ONE_SECTION) {
            throw new SectionNotDeleteException();
        }
    }

    public Section getUpperSection(long stationId) {
        return getSectionWithConditionToDelete(stationId, section -> section.getDownStationId().equals(stationId));
    }

    public Section getLowerSection(long stationId) {
        return getSectionWithConditionToDelete(stationId, section -> section.getUpStationId().equals(stationId));
    }

    private Section getSectionWithConditionToDelete(long stationId, Predicate<Section> condition) {
        return sections.stream()
                .filter(condition)
                .findAny()
                .orElseThrow(() -> new SectionNotDeleteException());
    }

    public void joinSection(Section sectionToJoin, Section sectionToDelete) {
        Long upStationId = sectionToDelete.getUpStationId();
        sectionToJoin.setUpStationId(upStationId);
        sectionToJoin.setDistance(sectionToDelete.getDistance() + sectionToJoin.getDistance());
    }

    public boolean isTerminalRegistration(Section section) {
        Map<TerminalStation, Long> terminalStations = findTerminalStations();
        return terminalStations.get(TerminalStation.UP).equals(section.getDownStationId())
                || terminalStations.get(TerminalStation.DOWN).equals(section.getUpStationId());
    }

    public boolean isAddStationInMiddle(Integer distance, Section section) {
        if (section == null) {
            return false;
        }
        if (distance >= section.getDistance()) {
            throw new SectionNotRegisterException();
        }
        return true;
    }

    public Section getSectionWithSameUpStation(Long upStationId) {
        return getSectionWithCondition(section -> section.getUpStationId().equals(upStationId));
    }

    public Section getSectionWithSameDownStation(Long downStationId) {
        return getSectionWithCondition(section -> section.getDownStationId().equals(downStationId));
    }

    public void validateCreateSection(Section sectionWithSameUpStation, Section sectionWithSameDownStation) {
        validateUpAndDownStationAllExist(sectionWithSameUpStation, sectionWithSameDownStation);
        validateUpAndDownStationNotAllExist(sectionWithSameUpStation, sectionWithSameDownStation);
    }


    private Section getSectionWithCondition(Predicate<Section> condition) {
        return sections.stream()
                .filter(condition)
                .findAny()
                .orElse(null);
    }

    private Map<TerminalStation, Long> findTerminalStations() {
        LinkedList<Long> sortedStations = getSorted();
        return Map.of(TerminalStation.UP, sortedStations.getFirst(), TerminalStation.DOWN, sortedStations.getLast());
    }

    private Map<Long, Long> getToDownSectionMap(List<Section> foundSections) {
        return foundSections.stream()
                .collect(Collectors.toMap(section -> section.getUpStationId(), section -> section.getDownStationId()));
    }

    private Map<Long, Long> getToUpSectionMap(List<Section> foundSections) {
        return foundSections.stream()
                .collect(Collectors.toMap(section -> section.getDownStationId(), section -> section.getUpStationId()));
    }

    private Long getAnyPivot() {
        return sections.get(0).getUpStationId();
    }

    private void addDownSections(Map<Long, Long> toDownSection, LinkedList<Long> result, Long iterator) {
        while (true) {
            Long downSectionId = toDownSection.get(iterator);
            if (downSectionId == null) {
                break;
            }
            result.addLast(downSectionId);
            iterator = downSectionId;
        }
    }

    private void addUpSections(Map<Long, Long> toUpSection, LinkedList<Long> result, Long iterator) {
        while (true) {
            Long upSectionId = toUpSection.get(iterator);
            if (upSectionId == null) {
                break;
            }
            result.addFirst(upSectionId);
            iterator = upSectionId;
        }
    }
    private void validateUpAndDownStationAllExist(Section sectionWithSameUpStation, Section sectionWithSameDownStation) {
        if (sectionWithSameUpStation != null && sectionWithSameDownStation != null) {
            throw new SectionNotRegisterException();
        }
    }

    private void validateUpAndDownStationNotAllExist(Section sectionWithSameUpStation, Section sectionWithSameDownStation) {
        if (sectionWithSameUpStation == null && sectionWithSameDownStation == null) {
            throw new SectionNotRegisterException();
        }
    }
}
