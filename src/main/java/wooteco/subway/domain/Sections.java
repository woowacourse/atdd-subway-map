package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> getSortedStations() {
        Section randomSection = findAnySection();
        Section topSection = findTopSection(randomSection);
        return createSortedStations(topSection);
    }

    private Section findAnySection() {
        return sections.stream()
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하는 구간 데이터가 없습니다."));
    }

    private Section findTopSection(final Section section) {
        if (isTopSection(section)) {
            return section;
        }
        return findTopSection(nextUpperSection(section));
    }

    private boolean isTopSection(final Section section) {
        return sections.stream()
                .noneMatch(existingSection -> existingSection.isUpperThan(section));
    }

    private Section nextUpperSection(final Section section) {
        return sections.stream()
                .filter(existingSection -> existingSection.isUpperThan(section))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하는 구간 데이터가 없습니다."));
    }

    private List<Station> createSortedStations(Section section) {
        final List<Station> stations = new ArrayList<>();
        while (!isLastSection(section)) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
            section = nextSection(section);
        }
        return stations.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean isLastSection(final Section section) {
        return sections.stream()
                .noneMatch(existingSection -> existingSection.isLowerThan(section));
    }

    private Section nextSection(final Section section) {
        return sections.stream()
                .filter(existingSection -> existingSection.isLowerThan(section))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("더이상 하행구간이 없습니다."));
    }

    public void addSection(final Section section) {
        validateAddableSection(section);
        if (isTopSection(section) || isLastSection(section)) {
            sections.add(section);
            return;
        }
        addSectionToMiddle(section);
    }

    private void validateAddableSection(final Section section) {
        if (hasNoConnectableSection(section)) {
            throw new IllegalArgumentException("연결 할 수 있는 상행역 또는 하행역이 없습니다.");
        }
        if (isAlreadyConnectedSection(section)) {
            throw new IllegalArgumentException("입력한 구간의 상행역과 하행역이 이미 모두 연결되어 있습니다.");
        }
    }

    private boolean hasNoConnectableSection(final Section section) {
        return sections.stream()
                .noneMatch(existingSection -> existingSection.isConnectedSection(section));
    }

    private boolean isAlreadyConnectedSection(final Section section) {
        return hasEqualUpStationWith(section) && hasEqualDownStationWith(section);
    }

    private boolean hasEqualUpStationWith(final Section section) {
        return sections.stream()
                .anyMatch(existingSection -> existingSection.equalsWithUpStation(section));
    }

    private boolean hasEqualDownStationWith(final Section section) {
        return sections.stream()
                .anyMatch(existingSection -> existingSection.equalsWithDownStation(section));
    }

    private void addSectionToMiddle(final Section section) {
        if (hasSameUpStation(section)) {
            updateSectionWithSameUpStation(section);
            sections.add(section);
            return;
        }
        updateSectionWithSameDownStation(section);
        sections.add(section);
    }

    private boolean hasSameUpStation(final Section section) {
        return sections.stream()
                .anyMatch(existingSection -> existingSection.hasSameUpStation(section));
    }

    private void updateSectionWithSameUpStation(final Section section) {
        final Section sectionWithSameUpStation = sections.stream()
                .filter(existingSection -> existingSection.hasSameUpStation(section))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 구간이 없습니다."));
        sectionWithSameUpStation.updateSectionWithSameUpStation(section);
    }

    private void updateSectionWithSameDownStation(final Section section) {
        final Section sectionWithSameDownStation = sections.stream()
                .filter(existingSection -> existingSection.hasSameDownStationWith(section))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 구간이 없습니다."));
        sectionWithSameDownStation.updateSectionWithSameDownStation(section);
    }
}
