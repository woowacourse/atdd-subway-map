package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Sections {

    private static final int LIMIT_REMOVE_SIZE = 1;

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
        validateSectionsSize();
    }

    private void validateSectionsSize() {
        if (this.sections.isEmpty()) {
            throw new IllegalArgumentException("sections는 크기가 0으로는 생성할 수 없습니다.");
        }
    }

    public Stations calculateSortedStations() {
        Section section = calculateFirstSection(findAnySection());
        return new Stations(createSortedStations(section));
    }

    private Section findAnySection() {
        return sections.stream()
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("section을 찾을 수 없습니다."));
    }

    private Section calculateFirstSection(final Section section) {
        if (!hasUpSection(section)) {
            return section;
        }
        return calculateFirstSection(calculateUpSection(section));
    }

    private boolean hasUpSection(final Section section) {
        return sections.stream()
                .anyMatch(section::isUpSection);
    }

    private Section calculateUpSection(final Section section) {
        return sections.stream()
                .filter(section::isUpSection)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("section을 찾을 수 없습니다."));
    }

    private List<Station> createSortedStations(Section section) {
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());

        while (hasDownSection(section)) {
            stations.add(section.getDownStation());
            section = downSection(section);
        }
        stations.add(section.getDownStation());
        return stations;
    }

    private boolean hasDownSection(final Section section) {
        return sections.stream()
                .anyMatch(section::isDownSection);
    }

    private Section downSection(final Section section) {
        return sections.stream()
                .filter(section::isDownSection)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("section을 찾을 수 없습니다."));
    }

    public void addSection(final Section section) {
        validateAdditionalSection(section);
        if (isUpperThanTopSection(section) || isLowerThanBottomSection(section)) {
            sections.add(section);
            return;
        }
        if (hasEqualsUpStation(section)) {
            addSectionByEqualsUpStation(section);
            return;
        }
        if (hasEqualsDownStation(section)) {
            addSectionByEqualsDownStation(section);
            return;
        }
        throw new RuntimeException("section 추가가 불가능한 상태입니다.");
    }

    private void validateAdditionalSection(final Section section) {
        if (hasNotUpStationOrDownStation(section)) {
            throw new IllegalStateException("구간 추가는 기존의 상행역 하행역 중 하나를 포함해야합니다.");
        }
        if (existUpStationToDownStation(section)) {
            throw new IllegalStateException("이미 상행에서 하행으로 갈 수 있는 구간이 존재합니다.");
        }
    }

    private void addSectionByEqualsDownStation(final Section section) {
        Section updatedSection = sections.stream()
                .filter(section::equalsDownStation)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("section을 찾을 수 없습니다."));
        if (updatedSection.isEqualsOrLargerDistance(section)) {
            throw new IllegalStateException("기존 길이보다 긴 구간은 중간에 추가될 수 없습니다.");
        }
        sections.add(section);
        sections.removeIf(updatedSection::equals);
        sections.add(updatedSection.createMiddleSectionByUpStationSection(section));
    }

    private void addSectionByEqualsUpStation(final Section section) {
        Section updatedSection = sections.stream()
                .filter(section::equalsUpStation)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("section을 찾을 수 없습니다."));
        if (updatedSection.isEqualsOrLargerDistance(section)) {
            throw new IllegalStateException("기존 길이보다 긴 구간은 중간에 추가될 수 없습니다.");
        }
        sections.add(section);
        sections.removeIf(updatedSection::equals);
        sections.add(updatedSection.createMiddleSectionByDownStationSection(section));
    }

    private boolean hasNotUpStationOrDownStation(final Section section) {
        return sections.stream()
                .noneMatch(section::isUpSectionOrDownSection);
    }

    private boolean existUpStationToDownStation(final Section section) {
        return hasEqualsUpStation(section) && hasEqualsDownStation(section);
    }

    private boolean hasEqualsUpStation(final Section section) {
        return sections.stream()
                .anyMatch(section::equalsUpStation);
    }

    private boolean hasEqualsDownStation(final Section section) {
        return sections.stream()
                .anyMatch(section::equalsDownStation);
    }

    private boolean isUpperThanTopSection(final Section section) {
        return calculateFirstSection(findAnySection()).isUpSection(section);
    }

    private boolean isLowerThanBottomSection(final Section section) {
        return calculateLastSection(findAnySection()).isDownSection(section);
    }

    private Section calculateLastSection(final Section section) {
        if (!hasDownSection(section)) {
            return section;
        }
        return calculateLastSection(calculateDownSection(section));
    }

    private Section calculateDownSection(final Section section) {
        return sections.stream()
                .filter(section::isDownSection)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("section을 찾을 수 없습니다."));
    }

    public Section removeSection(final Station station) {
        if (notContainsStation(station)) {
            throw new IllegalStateException("해당 역은 구간에 포함되어있지 않습니다.");
        }
        if (sections.size() == LIMIT_REMOVE_SIZE) {
            throw new IllegalStateException("구간이 하나뿐이어서 제거할 수 없습니다.");
        }
        if (isTopStation(station)) {
            return removeTopSection();
        }
        if (isBottomStation(station)) {
            return removeBottomSection();
        }
        return removeIntervalStation(station);
    }

    private boolean notContainsStation(final Station station) {
        return sections.stream()
                .noneMatch(section -> section.containsStation(station));
    }

    private boolean isTopStation(final Station station) {
        return calculateFirstSection(findAnySection()).isUpStation(station);
    }

    private Section removeTopSection() {
        Section topSection = calculateFirstSection(findAnySection());
        sections.removeIf(topSection::equals);
        return topSection;
    }

    private boolean isBottomStation(final Station station) {
        return calculateLastSection(findAnySection()).isDownStation(station);
    }

    private Section removeBottomSection() {
        Section bottomSection = calculateLastSection(findAnySection());
        sections.removeIf(bottomSection::equals);
        return bottomSection;
    }

    private Section removeIntervalStation(final Station station) {
        Section removeSection = findSectionByUpStation(station);
        Section updateSection = findSectionByDownStation(station);

        sections.removeIf(removeSection::equals);
        sections.removeIf(updateSection::equals);
        sections.add(updateSection.createExtensionSection(removeSection));
        return removeSection;
    }

    private Section findSectionByUpStation(final Station station) {
        return sections.stream()
                .filter(section -> section.isUpStation(station))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("section을 찾을 수 없습니다."));
    }

    private Section findSectionByDownStation(final Station station) {
        return sections.stream()
                .filter(section -> section.isDownStation(station))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("section을 찾을 수 없습니다."));
    }

    public List<Section> getSections() {
        return List.copyOf(sections);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
