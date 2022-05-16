package wooteco.subway.domain;

import lombok.AllArgsConstructor;
import wooteco.subway.exception.constant.SectionNotDeleteException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Sections {
    public static final int ONLY_ONE_SECTION = 1;

    private final List<Section> sections;

    public LinkedList<Long> getSorted() {
        Map<Long, Long> toDownSectionMap = getToDownSectionMap(sections);
        Map<Long, Long> toUpSectionMap = getToUpSectionMap(sections);

        LinkedList<Long> result = new LinkedList<>();
        Long pivot = getAnyPivot();
        result.add(pivot);
        addDownSections(toDownSectionMap, result, getAnyPivot());
        addUpSections(toUpSectionMap, result, getAnyPivot());
        return result;
    }

    public void validateCanDeleteSection() {
        if (sections.size() == ONLY_ONE_SECTION) {
            throw new SectionNotDeleteException();
        }
    }

    public Section getUpperSection(long stationId) {
        return sections.stream()
                .filter(it -> it.getDownStationId().equals(stationId))
                .findAny()
                .get();
    }

    public Section getLowerSection(long stationId) {
        return sections.stream()
                .filter(it -> it.getUpStationId().equals(stationId))
                .findAny()
                .get();
    }

    public void joinSection(Section sectionToJoin, Section sectionToDelete) {
        Long upStationId = sectionToDelete.getUpStationId();
        sectionToJoin.setUpStationId(upStationId);
        sectionToJoin.setDistance(sectionToDelete.getDistance() + sectionToJoin.getDistance());
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
}
