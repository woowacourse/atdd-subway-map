package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import wooteco.subway.exception.IllegalSectionException;

public class Sections {

    private static final int MINIMUM_SECTION_COUNT = 1;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> add(Section section) {
        checkContainsSameSection(section);
        preventFork(section);
        isPossibleRegistration(section);

        sections.add(section);

        return sections;
    }

    private void checkContainsSameSection(Section newSection) {
        final boolean isContains = sections.stream()
                .anyMatch(section -> section.isSameSection(newSection));

        if (isContains) {
            throw new IllegalSectionException("이미 동일한 구간이 등록되어 있습니다.");
        }
    }

    private void preventFork(Section newSection) {
        final Optional<Section> findSection = findForkSection(newSection);
        findSection.ifPresent(section -> processFork(section, newSection));
    }

    private Optional<Section> findForkSection(Section section) {
        return sections.stream()
                .filter(s -> s.isFork(section))
                .findAny();
    }

    private void processFork(Section existingSection, Section newSection) {
        checkDistance(existingSection, newSection);

        addSectionInMiddle(existingSection, newSection);

        sections.remove(existingSection);
    }

    private void checkDistance(Section existingSection, Section newSection) {
        if (existingSection.getDistance() <= newSection.getDistance()) {
            throw new IllegalSectionException("등록하려는 구간 길이가 기존 구간의 길이보다 더 길 수 없습니다.");
        }
    }

    private void addSectionInMiddle(Section existingSection, Section newSection) {
        Section section = createNewSection(existingSection, newSection);

        sections.add(section);
    }

    private Section createNewSection(Section existingSection, Section newSection) {
        if (existingSection.getUpStationId().equals(newSection.getUpStationId())) {
            return new Section(existingSection.getId(), existingSection.getLineId(), newSection.getDownStationId(),
                    existingSection.getDownStationId(), existingSection.getDistance() - newSection.getDistance());
        }

        return new Section(existingSection.getId(), existingSection.getLineId(), existingSection.getUpStationId(),
                newSection.getUpStationId(), existingSection.getDistance() - newSection.getDistance());
    }

    private void isPossibleRegistration(Section section) {
        sections.stream()
                .filter(s -> s.containsStation(section))
                .findAny()
                .orElseThrow(() -> new IllegalSectionException("등록할 구간의 적어도 하나의 역은 등록되어 있어야 합니다."));
    }

    public List<Section> delete(Long stationId) {
        validateThatHasMinimumSection();

        final Section previousSection = getPreviousSection(stationId);
        final Section laterSection = getLaterSection(stationId);

        findAndRemoveFirstSection(stationId, previousSection);
        findAndRemoveLastSection(stationId, laterSection);
        removeMiddleSection(previousSection, laterSection);

        return sections;
    }

    private void validateThatHasMinimumSection() {
        if (sections.size() <= MINIMUM_SECTION_COUNT) {
            throw new IllegalSectionException("노선이 구간을 하나는 가져야하므로 구간을 제거할 수 없습니다.");
        }
    }

    private void findAndRemoveFirstSection(Long stationId, Section section) {
        if (section == null) {
            final Section findSection = getFirstSection(stationId);
            sections.remove(findSection);
        }
    }

    private void findAndRemoveLastSection(Long stationId, Section section) {
        if (section == null) {
            final Section findSection = getLastSection(stationId);
            sections.remove(findSection);
        }
    }

    private Section getFirstSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalSectionException("첫역을 찾을 수 없습니다."));
    }

    private Section getLastSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalSectionException("마지막역을 찾을 수 없습니다."));
    }

    private void removeMiddleSection(Section previousSection, Section laterSection) {
        if (previousSection != null && laterSection != null) {
            deleteSection(previousSection, laterSection);
        }
    }

    private Section getPreviousSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(stationId))
                .findAny()
                .orElse(null);
    }

    private Section getLaterSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId))
                .findAny()
                .orElse(null);
    }

    private void deleteSection(Section previousSection, Section laterSection) {
        final int distance = previousSection.getDistance() + laterSection.getDistance();
        final Section newSection = new Section(previousSection.getLineId(), previousSection.getUpStationId(),
                laterSection.getDownStationId(), distance);

        sections.add(newSection);
        sections.remove(previousSection);
        sections.remove(laterSection);
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }

    public List<Section> getSortedSection() {
        final List<Section> copySections = new ArrayList<>(List.copyOf(sections));

        final Section firstSection = findFirstSection(copySections);

        List<Section> sortedSections = new ArrayList<>();
        sortedSections.add(firstSection);

        concatenateSection(sortedSections, copySections);
        
        return sortedSections;
    }

    private Section findFirstSection(List<Section> copySections) {
        return copySections.stream()
                .filter(section -> isFirst(section.getUpStationId()))
                .findAny()
                .orElseThrow();
    }

    private boolean isFirst(Long upStationId) {
        return sections.stream()
                .noneMatch(section -> upStationId.equals(section.getDownStationId()));
    }

    private void concatenateSection(List<Section> sortedSections, List<Section> copySections) {
        while (sortedSections.size() != copySections.size()) {
            final Section lastSection = sortedSections.get(sortedSections.size() - 1);
            final Long lastDownStationId = lastSection.getDownStationId();

            checkAndConcatenate(sortedSections, copySections, lastDownStationId);
        }
    }

    private void checkAndConcatenate(List<Section> sortedSections, List<Section> copySections, Long lastDownStationId) {
        for (Section section : copySections) {
            moveOneByOne(sortedSections, lastDownStationId, section);
        }
    }

    private void moveOneByOne(List<Section> sortedSections, Long lastDownStationId, Section section) {
        if (section.getUpStationId().equals(lastDownStationId)) {
            sortedSections.add(section);
        }
    }
}
