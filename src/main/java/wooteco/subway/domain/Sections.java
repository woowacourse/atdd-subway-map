package wooteco.subway.domain;

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

    public void delete(Station station) {
        final Long stationId = station.getId();

        final Section previousSection = getPreviousSection(stationId);
        final Section laterSection = getLaterSection(stationId);

        deleteSection(previousSection, laterSection);
    }

    private void deleteSection(Section previousSection, Section laterSection) {
        final int distance = previousSection.getDistance() + laterSection.getDistance();
        final Section newSection = new Section(previousSection.getLineId(), previousSection.getUpStationId(),
                laterSection.getDownStationId(), distance);

        sections.add(newSection);
        sections.remove(previousSection);
        sections.remove(laterSection);
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

    public List<Section> getSections() {
        return List.copyOf(sections);
    }
}
