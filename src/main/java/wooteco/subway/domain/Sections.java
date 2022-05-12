package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import wooteco.subway.exception.NotFoundException;

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

    public List<Station> calculateSortedStations() {
        return createSortedStations(calculateTopSection());
    }

    private List<Station> createSortedStations(Section section) {
        List<Station> stations = new ArrayList<>();
        stations.add(section.getUpStation());

        while (hasLowerSection(section)) {
            stations.add(section.getDownStation());
            section = lowerSection(section);
        }
        stations.add(section.getDownStation());
        return stations;
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
        addSectionByEqualsDownStation(section);
    }

    private void validateAdditionalSection(final Section section) {
        if (hasNotConnectedSection(section)) {
            throw new IllegalStateException("구간 추가는 기존의 상행역 하행역 중 하나를 포함해야합니다.");
        }
        if (existUpStationToDownStation(section)) {
            throw new IllegalStateException("이미 상행에서 하행으로 갈 수 있는 구간이 존재합니다.");
        }
    }

    private boolean hasNotConnectedSection(final Section section) {
        return sections.stream()
                .noneMatch(section::isConnectedSection);
    }

    private boolean existUpStationToDownStation(final Section section) {
        return hasEqualsUpStation(section) && hasEqualsDownStation(section);
    }

    private boolean hasEqualsDownStation(final Section section) {
        return sections.stream()
                .anyMatch(section::equalsDownStation);
    }

    private boolean isUpperThanTopSection(final Section section) {
        return calculateTopSection().isUpperSection(section);
    }

    private boolean isLowerThanBottomSection(final Section section) {
        return calculateLastSection().isLowerSection(section);
    }

    private void addSectionByEqualsUpStation(final Section section) {
        Section updatedSection = findSection(section::equalsUpStation);
        validateEqualsOrLargerDistance(section, updatedSection);
        sections.add(section);
        sections.removeIf(updatedSection::equals);
        sections.add(updatedSection.createMiddleSectionByDownStationSection(section));
    }

    private void addSectionByEqualsDownStation(final Section section) {
        Section updatedSection = findSection(section::equalsDownStation);
        validateEqualsOrLargerDistance(section, updatedSection);
        sections.add(section);
        sections.removeIf(updatedSection::equals);
        sections.add(updatedSection.createMiddleSectionByUpStationSection(section));
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
        return calculateTopSection().isUpStation(station);
    }

    private Section removeTopSection() {
        Section topSection = calculateTopSection();
        sections.removeIf(topSection::equals);
        return topSection;
    }

    private boolean isBottomStation(final Station station) {
        return calculateLastSection().isDownStation(station);
    }

    private Section removeBottomSection() {
        Section bottomSection = calculateLastSection();
        sections.removeIf(bottomSection::equals);
        return bottomSection;
    }

    private Section removeIntervalStation(final Station station) {
        Section removeSection = findSection(isUpStation(station));
        Section updateSection = findSection(isDownStations(station));

        sections.removeIf(removeSection::equals);
        sections.removeIf(updateSection::equals);
        sections.add(updateSection.createExtensionSection(removeSection));
        return removeSection;
    }

    private Predicate<Section> isUpStation(final Station station) {
        return section -> section.isUpStation(station);
    }

    private Predicate<Section> isDownStations(final Station station) {
        return section -> section.isDownStation(station);
    }

    private Section calculateTopSection() {
        return calculateTopSection(findAnySection());
    }

    private Section calculateTopSection(final Section section) {
        if (!hasUpperSection(section)) {
            return section;
        }
        return calculateTopSection(upperSection(section));
    }

    private boolean hasUpperSection(final Section section) {
        return sections.stream()
                .anyMatch(section::isUpperSection);
    }

    private Section upperSection(final Section section) {
        return sections.stream()
                .filter(section::isUpperSection)
                .findFirst()
                .orElseThrow(notFoundSectionSupplier());
    }

    private Section calculateLastSection() {
        return calculateLastSection(findAnySection());
    }

    private Section calculateLastSection(final Section section) {
        if (!hasLowerSection(section)) {
            return section;
        }
        return calculateLastSection(lowerSection(section));
    }

    private boolean hasLowerSection(final Section section) {
        return sections.stream()
                .anyMatch(section::isLowerSection);
    }

    private Section lowerSection(final Section section) {
        return sections.stream()
                .filter(section::isLowerSection)
                .findFirst()
                .orElseThrow(notFoundSectionSupplier());
    }

    private Section findAnySection() {
        return sections.stream()
                .findAny()
                .orElseThrow(notFoundSectionSupplier());
    }

    private boolean hasEqualsUpStation(final Section section) {
        return sections.stream()
                .anyMatch(section::equalsUpStation);
    }

    private Section findSection(final Predicate<Section> isDesiredSection) {
        return sections.stream()
                .filter(isDesiredSection)
                .findFirst()
                .orElseThrow(notFoundSectionSupplier());
    }

    private void validateEqualsOrLargerDistance(final Section section, final Section updatedSection) {
        if (updatedSection.isEqualsOrLargerDistance(section)) {
            throw new IllegalStateException("기존 길이보다 길거나 같은 구간은 중간에 추가될 수 없습니다.");
        }
    }

    private Supplier<NotFoundException> notFoundSectionSupplier() {
        return () -> new NotFoundException("section을 찾을 수 없습니다.");
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
