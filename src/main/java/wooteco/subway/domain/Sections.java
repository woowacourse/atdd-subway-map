package wooteco.subway.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private static final int FIND_FIRST_SECTION_INDEX = 0;
    private static final int FIND_SECOND_SECTION_INDEX = 1;
    private static final int DISTANCE_MINIMUM = 0;
    private static final int SECTIONS_MINIMUM = 1;
    private static final int FIND_FINAL_SECTION_SIZE = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section requestSection) {
        validateSection(requestSection);
        if (validateFinalSection(requestSection)) {
            saveFinalSection(requestSection);
            return;
        }
        saveMiddleSection(requestSection);
    }

    private void validateSection(Section requestSection) {
        final boolean isIncludedUpStation = sections.stream()
                .anyMatch(section -> section.getUpStationId().equals(requestSection.getUpStationId())
                        || section.getUpStationId().equals(requestSection.getDownStationId()));
        final boolean isIncludedDownStation = sections.stream()
                .anyMatch(section -> section.getDownStationId().equals(requestSection.getUpStationId())
                        || section.getDownStationId().equals(requestSection.getDownStationId()));

        if (isIncludedUpStation == true && isIncludedDownStation == true) {
            throw new IllegalArgumentException("이미 연결되어 있는 구간입니다.");
        }

        if (isIncludedUpStation == false && isIncludedDownStation == false) {
            throw new IllegalArgumentException("구간에 등록되지 않은 역입니다.");
        }
    }

    private boolean validateFinalSection(Section requestSection) {
        return (sections.get(FIND_FIRST_SECTION_INDEX).getUpStationId().equals(requestSection.getDownStationId())
                || sections.get(sections.size() - 1).getDownStationId().equals(requestSection.getUpStationId()));
    }

    private void saveFinalSection(Section requestSection) {
        if (sections.get(FIND_FIRST_SECTION_INDEX).getUpStationId().equals(requestSection.getDownStationId())) {
            sections.add(FIND_FIRST_SECTION_INDEX, requestSection);
            return;
        }
        sections.add(requestSection);
    }

    private void saveMiddleSection(Section requestSection) {
        if (isMiddleUpSection(requestSection)) {
            saveMiddleDownSection(requestSection);
            return;
        }
        if (isMiddleDownSection(requestSection)) {
            saveMiddleUpSection(requestSection);
            return;
        }

        throw new IllegalArgumentException("기존 노선에 등록할 수 없는 구간입니다.");
    }

    private boolean isMiddleUpSection(Section requestSection) {
        return sections.stream()
                .anyMatch(section -> section.getUpStationId().equals(requestSection.getUpStationId()));
    }

    private boolean isMiddleDownSection(Section requestSection) {
        return sections.stream()
                .anyMatch(section -> section.getDownStationId().equals(requestSection.getDownStationId()));
    }

    private void saveMiddleUpSection(Section requestSection) {
        final Section findSection = findIncludedSection(requestSection);
        final int index = sections.indexOf(findSection);
        final int changeDistance = findSection.getDistance() - requestSection.getDistance();
        validateDistance(changeDistance);

        final Section upSection = new Section(
                findSection.getLineId(), findSection.getUpStationId(), requestSection.getUpStationId(), changeDistance);
        sections.remove(index);
        sections.add(index, upSection);
        sections.add(index + 1, requestSection);
    }

    private void saveMiddleDownSection(Section requestSection) {
        final Section findSection = findIncludedSection(requestSection);
        final int index = sections.indexOf(findSection);
        final int changeDistance = findSection.getDistance() - requestSection.getDistance();
        validateDistance(changeDistance);

        final Section downSection = new Section(
                findSection.getLineId(), requestSection.getDownStationId(), findSection.getDownStationId(), changeDistance);
        sections.remove(index);
        sections.add(index, requestSection);
        sections.add(index + 1, downSection);
    }

    private void validateDistance(int changeDistance) {
        if (changeDistance <= DISTANCE_MINIMUM) {
            throw new IllegalArgumentException("등록할 수 없는 거리 입니다.");
        }
    }

    private Section findIncludedSection(Section requestSection) {
        final Section findSection = sections.stream()
                .filter(section -> section.getDownStationId().equals(requestSection.getDownStationId())
                        || section.getUpStationId().equals(requestSection.getUpStationId()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 구역입니다."));
        return findSection;
    }

    public void remove(Long lineId, Long stationId) {
        validateSectionSize();
        final List<Section> findSections = findIncludedByStationId(stationId);
        validateFindSectionSize(findSections);
        if (findSections.size() == FIND_FINAL_SECTION_SIZE) {
            removeFinalSection(findSections);
            return;
        }
        removeMiddleSection(lineId, findSections);
    }
    private void validateSectionSize() {
        if (sections.size() == SECTIONS_MINIMUM) {
            throw new IllegalArgumentException("구간은 1개 이상이 있어야 합니다.");
        }
    }

    private List<Section> findIncludedByStationId(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId)
                        || section.getDownStationId().equals(stationId))
                .collect(Collectors.toList());
    }

    private void validateFindSectionSize(List<Section> findSections) {
        if (findSections.size() == 0) {
            throw new IllegalArgumentException("해당 역과 관련된 구간이 존재하지 않습니다.");
        }
    }

    private void removeFinalSection(List<Section> findSections) {
        sections.removeAll(findSections);
    }

    private void removeMiddleSection(Long lineId, List<Section> findSections) {
        final int updateDistance = findSections.get(FIND_FIRST_SECTION_INDEX).getDistance() + findSections.get(FIND_SECOND_SECTION_INDEX).getDistance();
        final Section updateSection = new Section(
                lineId, findSections.get(FIND_FIRST_SECTION_INDEX).getUpStationId(), findSections.get(FIND_SECOND_SECTION_INDEX).getDownStationId(), updateDistance);

        final int index = sections.indexOf(findSections.get(FIND_FIRST_SECTION_INDEX));
        sections.add(index, updateSection);
        sections.removeAll(findSections);
    }



    public List<Section> getSections() {
        return sections;
    }
}
