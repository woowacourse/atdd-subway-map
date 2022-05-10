package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import wooteco.subway.exception.IllegalSectionException;

public class Sections {

    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section section) {
        checkContainsSameSection(section);
        preventFork(section);
        isPossibleRegistration(section);

        sections.add(section);
    }

    private void checkContainsSameSection(Section section) {
        for (Section existingSection : sections) {
            processSameSection(section, existingSection);
        }
    }

    private void processSameSection(Section section, Section existingSection) {
        if (existingSection.isSameSection(section)) {
            throw new IllegalSectionException("이미 구간이 등록되어 있습니다.");
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
        final Section section = new Section(existingSection.getId(), existingSection.getLineId(), newSection.getDownStationId(),
                existingSection.getDownStationId(), existingSection.getDistance() - newSection.getDistance());

        sections.add(section);
        sections.remove(existingSection);
    }

    private void checkDistance(Section existingSection, Section newSection) {
        if (existingSection.getDistance() <= newSection.getDistance()) {
            throw new IllegalSectionException("구간 등록이 불가능합니다.");
        }
    }

    private void isPossibleRegistration(Section section) {
        sections.stream()
                .filter(s -> s.isContainStation(section))
                .findAny()
                .orElseThrow(() -> new IllegalSectionException("구간 등록이 불가능합니다."));
    }

    public void delete(Long stationId) {
        validateDeletion();

        final Section previousSection = getPreviousSection(stationId);
        final Section laterSection = getLaterSection(stationId);

        deleteSection(previousSection, laterSection);
    }

    private void validateDeletion() {
        if (sections.size() <= 1) {
            throw new IllegalSectionException("구간을 제거할 수 없습니다.");
        }
    }

    private Section getPreviousSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalSectionException("삭제할 구간이 존재하지 않습니다."));
    }

    private Section getLaterSection(Long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalSectionException("삭제할 구간이 존재하지 않습니다."));
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
