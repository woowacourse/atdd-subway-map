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
        final Section topSection = findTopSection(findAnySection());
        return createSortedStations(topSection);
    }

    private Section findAnySection() {
        return sections.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하는 구간 데이터가 없습니다."));
    }

    private Section findTopSection(final Section section) {
        if (isTopSection(section)) {
            return section;
        }
        return findTopSection(nextUpperSection(section));
    }

    private boolean isTopSection(final Section otherSection) {
        return sections.stream()
                .noneMatch(section -> section.isUpperThan(otherSection));
    }

    private Section nextUpperSection(final Section otherSection) {
        return sections.stream()
                .filter(section -> section.isUpperThan(otherSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하는 구간 데이터가 없습니다."));
    }

    private List<Station> createSortedStations(final Section section) {
        final List<Station> stations = new ArrayList<>();
        addStationInSection(stations, section);
        return stations.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private void addStationInSection(final List<Station> stations, final Section section) {
        stations.add(section.getUpStation());
        stations.add(section.getDownStation());
        if (isLastSection(section)) {
            return;
        }
        addStationInSection(stations, nextLowerSection(section));
    }

    private boolean isLastSection(final Section otherSection) {
        return sections.stream()
                .noneMatch(section -> section.isLowerThan(otherSection));
    }

    private Section nextLowerSection(final Section otherSection) {
        return sections.stream()
                .filter(section -> section.isLowerThan(otherSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("더이상 하행구간이 없습니다."));
    }

    public void addSection(final Section section) {
        validateAddableSection(section);
        if (isOverThanTopSection(section) || isUnderThanLastSection(section)) {
            this.sections.add(section);
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

    private boolean hasNoConnectableSection(final Section otherSection) {
        return sections.stream()
                .noneMatch(section -> section.isConnectedWith(otherSection));
    }

    private boolean isAlreadyConnectedSection(final Section section) {
        return isConnectedWithUpStationOf(section) && isConnectedWithDownStationOf(section);
    }

    private boolean isConnectedWithUpStationOf(final Section otherSection) {
        return sections.stream()
                .anyMatch(section -> section.hasSameStationWith(otherSection.getUpStation()));
    }

    private boolean isConnectedWithDownStationOf(final Section otherSection) {
        return sections.stream()
                .anyMatch(section -> section.hasSameStationWith(otherSection.getDownStation()));
    }

    private boolean isOverThanTopSection(final Section section) {
        final Section topSection = findTopSection(findAnySection());
        return topSection.isLowerThan(section);
    }

    private boolean isUnderThanLastSection(final Section section) {
        final Section lastSection = findLastSection(findAnySection());
        return lastSection.isUpperThan(section);
    }

    private Section findLastSection(final Section section) {
        if (isLastSection(section)) {
            return section;
        }
        return findLastSection(nextLowerSection(section));
    }

    private void addSectionToMiddle(final Section otherSection) {
        if (hasSameUpStationWith(otherSection)) {
            updateUpStationOfSectionFrom(otherSection);
            sections.add(otherSection);
            return;
        }
        updateDownStationOfSectionFrom(otherSection);
        sections.add(otherSection);
    }

    private boolean hasSameUpStationWith(final Section otherSection) {
        return sections.stream()
                .anyMatch(section -> section.hasSameUpStationWith(otherSection.getUpStation()));
    }

    private void updateUpStationOfSectionFrom(final Section otherSection) {
        final Section section = sections.stream()
                .filter(existingSection -> existingSection.hasSameUpStationWith(otherSection.getUpStation()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("입력한 역정보를 가지는 구간이 없습니다."));
        section.updateUpStationFrom(otherSection);
    }

    private void updateDownStationOfSectionFrom(final Section otherSection) {
        final Section section = sections.stream()
                .filter(existingSection -> existingSection.hasSameDownStationWith(otherSection.getDownStation()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("입력한 역정보를 가지는 구간이 없습니다."));
        section.updateDownStationFrom(otherSection);
    }

    public void deleteSection(final Station station) {
        validateEnoughToDeleteSection();
        if (isTopStation(station)) {
            deleteTopSectionBy(station);
            return;
        }
        if (isLastStation(station)) {
            deleteLastSectionBy(station);
            return;
        }
        deleteMiddleSectionBy(station);
    }

    private void validateEnoughToDeleteSection() {
        if (sections.size() == 1) {
            throw new RuntimeException("현재 구간이 하나 있기때문에, 구간을 제거 할수 없습니다.");
        }
    }

    private boolean isTopStation(final Station station) {
        return sections.stream()
                .anyMatch(section -> section.hasSameUpStationWith(station))
                && sections.stream()
                .noneMatch(section -> section.hasSameDownStationWith(station));
    }

    private void deleteTopSectionBy(final Station station) {
        final Section topSection = sections.stream()
                .filter(existingSection -> existingSection.hasSameUpStationWith(station))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("입력한 역정보를 가지는 구간이 없습니다."));
        sections.remove(topSection);
    }

    private boolean isLastStation(final Station station) {
        return sections.stream()
                .noneMatch(section -> section.hasSameUpStationWith(station))
                && sections.stream()
                .anyMatch(section -> section.hasSameDownStationWith(station));
    }

    private void deleteLastSectionBy(final Station station) {
        final Section lastSection = sections.stream()
                .filter(existingSection -> existingSection.hasSameDownStationWith(station))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("입력한 역정보를 가지는 구간이 없습니다."));
        sections.remove(lastSection);
    }

    private void deleteMiddleSectionBy(final Station station) {
        final Section upperSection = getUpperSection(station);
        final Section lowerSection = getLowerSection(station);
        upperSection.combineSection(lowerSection);
        sections.remove(lowerSection);
    }

    private Section getUpperSection(final Station station) {
        return sections.stream()
                .filter(existingSection -> existingSection.hasSameDownStationWith(station))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("입력한 역정보를 가지는 구간이 없습니다."));
    }

    private Section getLowerSection(final Station station) {
        return sections.stream()
                .filter(existingSection -> existingSection.hasSameUpStationWith(station))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("입력한 역정보를 가지는 구간이 없습니다."));
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }
}
